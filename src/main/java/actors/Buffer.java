package actors;

import org.jcsp.lang.*;

/** Buffer class: Manages communication between Producer2
 * and Consumer2 classes.
 */

public class Buffer implements CSProcess {
    private final AltingChannelInputInt[] itemIn;
    private final AltingChannelInputInt[] reqIn;
    private final ChannelOutputInt[] itemOut;
    private final ChannelOutputInt[] reqOut;

    private final int shift;

    private int runningPredecessors;
    private int runningSuccessors;

    private final int[] buffer;
    private final int bufferSize;
    int putIn = 0;
    int takeFrom = 0;


    public Buffer(int size,  final ChannelOutputInt[] reqOut, final AltingChannelInputInt itemIn[],
                  final AltingChannelInputInt[] reqIn, final ChannelOutputInt[] itemOut) {
        this.buffer = new int[size];
        this.bufferSize = size;

        this.reqIn = reqIn;
        this.itemIn = itemIn;

        this.reqOut = reqOut;
        this.itemOut = itemOut;
        this.shift = itemIn.length;;

        runningPredecessors = 1;
        runningSuccessors = itemOut.length;
    }

    private Guard[] getGuards() {
        Guard[] guards = new Guard[itemIn.length + reqIn.length];
        int i = 0;
        for (Guard g : itemIn) {
            guards[i] = g;
            i++;
        }
        for (Guard g : reqIn) {
            guards[i] = g;
            i++;
        }
        return guards;
    }

    public void run() {
        final Guard[] guards = getGuards();
        final Alternative alt = new Alternative(guards);

        int index;
        int item;
        for (int i=0; i<reqOut.length; i++) {
            reqOut[i].write(0);
        }

        while (runningSuccessors > 0 || runningPredecessors > 0) {
            index = alt.select();

            if (index < shift) {
                if (putIn <= takeFrom + bufferSize) {
                    item = itemIn[index].read();
                    System.out.println("from p " + index + ": " + item);
                    if (item < 0)
                        runningPredecessors--;
                    else {
                        buffer[putIn % buffer.length] = item;
                        putIn++;
                        reqOut[index].write(0);
                    }
                } else if (runningSuccessors == 0) {
                    //TODO send information to producer
                }

            } else {
                if (takeFrom < putIn) {
                    reqIn[index - shift].read();
                    item = buffer[takeFrom % buffer.length];
                    takeFrom++;
                    itemOut[index - shift].write(item);

                } else if (runningPredecessors == 0) {
                    System.out.println(index - shift + " " + -1);

                    reqIn[index - shift].read();
                    itemOut[index - shift].write(-1);
                    runningSuccessors--;
                }
            }
        }
        System.out.println("Buffer ended.");
    }
}