package actors;

import org.jcsp.lang.*;


public class Consumer extends Actor implements CSProcess {
    private final ChannelInputInt itemIn;
    private final ChannelOutputInt reqOut;

    private final int index;

    public Consumer(int index, ChannelOutputInt reqOut, ChannelInputInt itemIn, int delay) {
        super(delay);
        this.index = index;
        this.reqOut = reqOut;
        this.itemIn = itemIn;
    }

    public void run() {
        int item;
        while (true) {
            reqOut.write(Codes.REQ.value);
            item = itemIn.read();
            if (item == Codes.END.value)
                break;
            this.actorState.incrementPassedItems();
            sleepActor();
//            System.out.println("c:" + index + " received " + item);
        }
//        System.out.println("Consumer " + index + " ended.");
    }
}