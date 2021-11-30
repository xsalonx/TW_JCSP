package actors;

import org.jcsp.lang.*;
/** Consumer class: reads ints from input channel, displays them,
 then
 * terminates when a negative value is read.
 */
public class Consumer implements CSProcess {
    private final ChannelInputInt in;
    private final ChannelOutputInt req;

    private final int index;

    public Consumer(int index, final ChannelOutputInt req, final ChannelInputInt in) {
        this.index = index;
        this.req = req;
        this.in = in;
    }

    public void run() {
        int item;
        while (true) {
            req.write(0);
            item = in.read();
            if (item < 0)
                break;
            System.out.println("c " + index + ": " + item);
        }
        System.out.println("Consumer " + index + " ended.");
    }
}