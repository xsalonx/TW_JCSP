package actors;

import org.jcsp.lang.*;
/** Buffer class: Manages communication between Producer2
 * and Consumer2 classes.
 */

public class Buffer implements CSProcess {
    private final AltingChannelInputInt[] productionIn;
    private final AltingChannelInputInt[] reqIn;
    private final ChannelOutputInt[] consumptionOut;

    private int runningProducers;
    private int runningConsumers;

    private final int[] buffer;
    private final int bufferSize;
    int hd = 0;
    int tl = 0;

    public Buffer(int size, final AltingChannelInputInt[] productionIn, final
    AltingChannelInputInt[] reqIn, final ChannelOutputInt[] consumptionOut) {
        this.buffer = new int[size];
        this.bufferSize = size;

        this.productionIn = productionIn;
        this.reqIn = reqIn;
        this.consumptionOut = consumptionOut;

        runningProducers = productionIn.length;
        runningConsumers = consumptionOut.length;
    }

    public void run() {
        final Guard[] guards = {productionIn[0], productionIn[1], reqIn[0], reqIn[1]};
        final Alternative alt = new Alternative(guards);
        while (runningConsumers > 0 || runningProducers > 0) {
            int index = alt.select();
            switch (index) {
                case 0:
                case 1:
                    if (hd < tl + bufferSize) {
                        int item = productionIn[index].read();
                        System.out.println("from p " + index + ": " + item);
                        if (item < 0)
                            runningProducers--;
                        else {
                            buffer[hd % buffer.length] = item;
                            hd++;
                        }
                    } else if (runningConsumers == 0) {
                        // send information to producer
                    }
                    break;
                case 2:
                case 3:
                    if (tl < hd) {
                        reqIn[index - 2].read();
                        int item = buffer[tl % buffer.length];
                        tl++;
                        consumptionOut[index - 2].write(item);
                    } else if (runningProducers == 0) {
                        reqIn[index - 2].read();
                        consumptionOut[index - 2].write(-1);
                        System.out.println(index + " " + -1);
                        runningConsumers--;
                    }
                    break;
            }
        }
        System.out.println("Buffer ended.");
    }
}