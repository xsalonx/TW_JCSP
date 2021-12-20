package actors;

import org.jcsp.lang.*;
/** Consumer class: reads ints from input channel, displays them,
 then
 * terminates when a negative value is read.
 */
public class Consumer implements CSProcess {
    private final ChannelInputInt itemIn;
    private final ChannelOutputInt reqOut;

    private final int index;

    public Consumer(int index, final ChannelOutputInt reqOut, final ChannelInputInt itemIn) {
        this.index = index;
        this.reqOut = reqOut;
        this.itemIn = itemIn;
    }

    public void run() {
        int item;
        while (true) {
            reqOut.write(0);
            System.out.println("c " + index + " sent req");
            item = itemIn.read();
            System.out.println("c " + index + " item: " + item);
            if (item < 0)
                break;
        }
        System.out.println("Consumer " + index + " ended.");
    }
}