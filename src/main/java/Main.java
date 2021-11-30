
import actors.Buffer;
import actors.Consumer;
import actors.Producer;
import org.jcsp.lang.*;
/** Main program class for Producer/Consumer example.
 * Sets up channels, creates processes then
 * executes them in parallel, using JCSP.
 */
public final class Main {
    public static void main(String[] args) {

        final One2OneChannelInt[] prodChan_ = Channel.one2oneIntArray(2);
        final One2OneChannelInt[] consReq_ = Channel.one2oneIntArray(2);
        final One2OneChannelInt[] consChan_ = Channel.one2oneIntArray(2);

        final ChannelOutputInt[] prodChan = Channel.getOutputArray(prodChan_);
        final AltingChannelInputInt[] prodChanAl = Channel.getInputArray(prodChan_);
        final AltingChannelInputInt[] consReq = Channel.getInputArray(consReq_);
        final ChannelOutputInt[] conseeReq = Channel.getOutputArray(consReq_);

        final ChannelOutputInt[] consChan = Channel.getOutputArray(consChan_);
        final ChannelInputInt[] conseeChan = Channel.getInputArray(consChan_);


        final int productionsNumber = 10;
        CSProcess[] procList = {
                new Producer(0, prodChan[0], 0, productionsNumber),
                new Producer(1, prodChan[1], 100, productionsNumber),
                new Buffer(prodChanAl, consReq, consChan),
                new Consumer(0, conseeReq[0], conseeChan[0]),
                new Consumer(1, conseeReq[1], conseeChan[1])
        };

        Parallel par = new Parallel(procList);
        par.run();

    }
}