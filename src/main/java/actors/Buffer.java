package actors;

import org.jcsp.lang.*;
/** Buffer class: Manages communication between Producer2
 * and Consumer2 classes.
 */

public class Buffer implements CSProcess {
    private final AltingChannelInputInt[] in; // Input from Producer
    private final AltingChannelInputInt[] req; // Request for data from Consumer
    private final ChannelOutputInt[] out; // Output to Consumer

    private final int[] buffer = new int[10];
    // Subscripts for buffer
    int hd = -1;
    int tl = -1;

    public Buffer(final AltingChannelInputInt[] in, final
    AltingChannelInputInt[] req, final ChannelOutputInt[] out) {
        this.in = in;
        this.req = req;
        this.out = out;
    }

    public void run() {
        final Guard[] guards = {in[0], in[1], req[0], req[1]};
        final Alternative alt = new Alternative(guards);
        int runningActors = 4; // Number of processes running
        while (runningActors > 0) {
            int index = alt.select();
            switch (index) {
                case 0:
                case 1: // A Producer is ready to send
                    if (hd < tl + 11) // Space available
                    {
                        int item = in[index].read();
                        if (item < 0)
                            runningActors--;
                        else {
                            hd++;
                            buffer[hd % buffer.length] = item;
                        }
                    }
                    break;
                case 2:
                case 3: // A Consumer is ready to read
                    if (tl < hd) // Item(s) available
                    {
                        req[index - 2].read(); // Read and discard request
                        tl++;
                        int item = buffer[tl % buffer.length];
                        out[index - 2].write(item);
                    } else if (runningActors <= 2) // Signal consumer to end
                    {
                        req[index - 2].read(); // Read and discard request
                        out[index - 2].write(-1); // Signal end
                        runningActors--;
                    }
                    break;
            }
        }
        System.out.println("Buffer ended.");
    }
}