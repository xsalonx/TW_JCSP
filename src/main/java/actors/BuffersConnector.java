package actors;

import org.jcsp.lang.*;

public class BuffersConnector implements CSProcess{

    private int from;
    private int to;
    private ChannelInputInt reqIn;
    private ChannelInputInt productionIn;
    private ChannelOutputInt reqOut;
    private ChannelOutputInt productionOut;

    public BuffersConnector(int from, int to, final ChannelInputInt productionIn, final ChannelOutputInt productionOut,
                            ChannelOutputInt reqOut, final ChannelInputInt reqIn) {
        this.from = from;
        this.to = to;
        this.reqIn = reqIn;
        this.reqOut = reqOut;
        this.productionIn = productionIn;
        this.productionOut = productionOut;
    }

    public void run() {
        int item;
        while (true) {
            item = reqIn.read();
            if (item < 0)
                break;
            item = productionIn.read();
            productionOut.write(item);
            if (item < 0)
                break;
        }
        System.out.println("connector from " + from + " to " + to + " ended");
    }
}
