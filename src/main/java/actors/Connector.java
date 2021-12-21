package actors;

import org.jcsp.lang.*;


public class Connector  implements CSProcess{


    private final ChannelInputInt reqIn;
    private final ChannelOutputInt reqOut;

    private final ChannelInputInt itemIn;
    private final ChannelOutputInt itemOut;


    public Connector(ChannelOutputInt reqOut, ChannelInputInt itemIn,
                     ChannelInputInt reqIn, ChannelOutputInt itemOut) {
        this.reqIn = reqIn;
        this.reqOut = reqOut;
        this.itemIn = itemIn;
        this.itemOut = itemOut;
    }

    public void run() {
        int item;
        do {
            item = reqIn.read();
            reqOut.write(item);
            item = itemIn.read();
            itemOut.write(item);
        } while (item != Actor.Codes.END.value);
    }
}
