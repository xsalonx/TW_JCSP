package actors;

import org.jcsp.lang.*;


public class Producer extends Actor implements CSProcess {

    private final ChannelOutputInt itemOut;
    private final ChannelInputInt reqIn;
    private final int index;
    private final int productionsNumber;

    public Producer(int index, ChannelInputInt reqIn, ChannelOutputInt itemOut, int productionsNumber, int delay) {
        super(delay);
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
            itemOut.write(item);
            this.actorState.incrementPassedItems();

            System.out.println("p " + (k+1) + "/" + productionsNumber + " " + index + " sent: " + item);
        }
        reqIn.read();
        itemOut.write(Codes.REQ.value);
//        System.out.println("producer " + index + " ended.");
    }

}

