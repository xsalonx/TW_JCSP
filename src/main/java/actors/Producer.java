package actors;

import org.jcsp.lang.*;


public class Producer implements CSProcess {

    private final ChannelOutputInt itemOut;
    private final ChannelInputInt reqIn;
    private final int index;
    private final int productionsNumber;

    public Producer(int index, final ChannelInputInt reqIn, final ChannelOutputInt itemOut, int productionsNumber) {
        this.itemOut = itemOut;
        this.reqIn = reqIn;
        this.index = index;
        this.productionsNumber = productionsNumber;
    }

    public void run() {
        int item;
        for (int k = 0; k < productionsNumber; k++) {
            reqIn.read();
            item = (int) (Math.random() * 100) + 1;
            System.out.println("p " + index + ": " + item);
            itemOut.write(item);
        }
        itemOut.write(-1);
        System.out.println("Producer " + index + " ended.");
    }

}

