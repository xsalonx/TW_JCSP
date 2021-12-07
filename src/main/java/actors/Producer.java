package actors;

import org.jcsp.lang.*;


public class Producer implements CSProcess {

    private final ChannelOutputInt channel;
    private final int index;
    private final int productionsNumber;


    public Producer(int index, final ChannelOutputInt productionsOut, int productionsNumber) {
        channel = productionsOut;
        this.index = index;
        this.productionsNumber = productionsNumber;
    }

    public void run() {
        int item;
        for (int k = 0; k < productionsNumber; k++) {
            item = (int) (Math.random() * 100) + 1;
            channel.write(item);
        }
        channel.write(-1);
        System.out.println("Producer " + index + " ended.");
    }

}

