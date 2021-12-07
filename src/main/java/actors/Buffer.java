package actors;

import org.jcsp.lang.*;

import java.util.ServiceConfigurationError;

/** Buffer class: Manages communication between Producer2
 * and Consumer2 classes.
 */

public class Buffer implements CSProcess {
    private final AltingChannelInputInt[] productionIn;
    private final int productionInShift = 0;
    private final AltingChannelInputInt[] reqIn;
    private final int reqInShift;
    private final ChannelOutputInt[] consumptionOut;

    private int runningProducers;
    private int runningConsumers;

    private final int[] buffer;
    private final int bufferSize;
    int putIn = 0;
    int takeFrom = 0;


    public Buffer(int size, final AltingChannelInputInt[] productionIn, final
    AltingChannelInputInt[] reqIn, final ChannelOutputInt[] consumptionOut) {
        this.buffer = new int[size];
        this.bufferSize = size;

        this.productionIn = productionIn;
        this.reqIn = reqIn;
        reqInShift = productionIn.length;
        this.consumptionOut = consumptionOut;

        runningProducers = productionIn.length;
        runningConsumers = consumptionOut.length;
    }

    private Guard[] getGuards() {
        Guard[] guards = new Guard[productionIn.length + reqIn.length];
        int i = 0;
        for (Guard g : productionIn) {
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
        while (runningConsumers > 0 || runningProducers > 0) {
            index = alt.select();

            if (index < productionIn.length) {
                if (putIn <= takeFrom + bufferSize) {
                    int item = productionIn[index].read();
                    System.out.println("from p " + index + ": " + item);
                    if (item < 0)
                        runningProducers--;
                    else {
                        buffer[putIn % buffer.length] = item;
                        putIn++;
                    }
                } else if (runningConsumers == 0) {
                    // send information to producer
                }
            } else {
                if (takeFrom < putIn) {
                    reqIn[index - reqInShift].read();
                    int item = buffer[takeFrom % buffer.length];
                    takeFrom++;
                    consumptionOut[index - reqInShift].write(item);
                } else if (runningProducers == 0) {
                    reqIn[index - reqInShift].read();
                    consumptionOut[index - reqInShift].write(-1);
                    System.out.println(index + " " + -1);
                    runningConsumers--;
                }
            }
        }
        System.out.println("Buffer ended.");
    }
}