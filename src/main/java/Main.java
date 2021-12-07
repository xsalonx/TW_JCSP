
import actors.Buffer;
import actors.Consumer;
import actors.Producer;
import org.jcsp.lang.*;



public final class Main {
    // Define color constants
    public static final String TEXT_RESET = "\u001B[0m";
    public static final String TEXT_BLACK = "\u001B[30m";
    public static final String TEXT_RED = "\u001B[31m";
    public static final String TEXT_GREEN = "\u001B[32m";
    public static final String TEXT_YELLOW = "\u001B[33m";
    public static final String TEXT_BLUE = "\u001B[34m";
    public static final String TEXT_PURPLE = "\u001B[35m";
    public static final String TEXT_CYAN = "\u001B[36m";
    public static final String TEXT_WHITE = "\u001B[37m";

    public static void main(String[] args) {

        final One2OneChannelInt[] productionsChannels = Channel.one2oneIntArray(2);
        final One2OneChannelInt[] requestChannels = Channel.one2oneIntArray(2);
        final One2OneChannelInt[] consumptionChannels = Channel.one2oneIntArray(2);

        final ChannelOutputInt[] productionsOut = Channel.getOutputArray(productionsChannels);
        final AltingChannelInputInt[] productionIn = Channel.getInputArray(productionsChannels);
        final AltingChannelInputInt[] reqIn = Channel.getInputArray(requestChannels);
        final ChannelOutputInt[] reqOut = Channel.getOutputArray(requestChannels);

        final ChannelOutputInt[] consumptionOut = Channel.getOutputArray(consumptionChannels);
        final ChannelInputInt[] consumptionIn = Channel.getInputArray(consumptionChannels);


        final int productionsNumber = 10;
        final int bufferSize = 10;

        CSProcess[] actorsList = {
                new Producer(0, productionsOut[0], 0, productionsNumber),
                new Producer(1, productionsOut[1], 100, productionsNumber),
                new Buffer(bufferSize, productionIn, reqIn, consumptionOut),
                new Consumer(0, reqOut[0], consumptionIn[0]),
                new Consumer(1, reqOut[1], consumptionIn[1])
        };

        Parallel par = new Parallel(actorsList);
        par.run();

    }
}