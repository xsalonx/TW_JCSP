package actors;

import org.jcsp.lang.*;
/** Producer class: produces 100 random integers and sends them on
 * output channel, then sends -1 and terminates.
 * The random integers are in a given range [start...start+100)
 */
public class Producer implements CSProcess {
    private static final int defaultProductionsNumber = 100;

    private final ChannelOutputInt channel;
    private final int start;
    private final int index;
    private final int productionsNumber;

    public Producer(int index, final ChannelOutputInt out, int start) {
        channel = out;
        this.index = index;
        this.start = start;
        productionsNumber = defaultProductionsNumber;
    }

    public Producer(int index, final ChannelOutputInt out, int start, int productionsNumber) {
        channel = out;
        this.index = index;
        this.start = start;
        this.productionsNumber = productionsNumber;
    }

    public void run() {
        int item;
        for (int k = 0; k < productionsNumber; k++) {
            item = (int) (Math.random() * 100) + 1 + start;
            System.out.println("p " + index + ": " + item);
            channel.write(item);
        }
        channel.write(-1);
        System.out.println("Producer " + index + " ended.");
    }

}

