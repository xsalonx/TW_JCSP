package actors;

import org.jcsp.lang.*;


public class Consumer extends Actor implements CSProcess {
    private final ChannelInputInt itemIn;
    private final ChannelOutputInt reqOut;

    private final int index;

    public Consumer(int index, ChannelOutputInt reqOut, ChannelInputInt itemIn) {
        super();
        this.index = index;
        this.reqOut = reqOut;
        this.itemIn = itemIn;
    }

    public void run() {
        int item;
        while (true) {
            reqOut.write(0);
            item = itemIn.read();
            System.out.println("c " + index + " item: " + item);
            if (item < 0)
                break;
            this.actorState.incrementPassedItems();
        }
        System.out.println("Consumer " + index + " ended.");
    }
}