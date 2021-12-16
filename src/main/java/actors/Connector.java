package actors;

import org.jcsp.lang.*;

public class Connector implements CSProcess{

    private final int from;
    private final int to;

    private final ChannelInputInt reqIn;
    private final ChannelOutputInt reqOut;

    private final ChannelInputInt itemIn;
    private final ChannelOutputInt itemOut;


    public Connector(int from, int to, final ChannelInputInt itemIn, final ChannelOutputInt itemOut,
                     ChannelOutputInt reqOut, final ChannelInputInt reqIn) {
        this.from = from;
        this.to = to;
        this.reqIn = reqIn;
        this.reqOut = reqOut;
        this.itemIn = itemIn;
        this.itemOut = itemOut;
    }

    public void run() {
        int item;
        while (true) {
            item = reqIn.read();
            System.out.println("connector req in " + item);
            if (item < 0)
                break;
            reqOut.write(1);
            System.out.println("connector req out ");
            item = itemIn.read();
            System.out.println("connector item in " + item);
            itemOut.write(item);
            if (item < 0)
                break;
        }
        System.out.println("connector from " + from + " to " + to + " ended");
    }
}
