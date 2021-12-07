
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
        final int producersNumber = 10;
        final int consumersNumber = 10;
        final int bufferSize = 20;
        final int productionsNumber = 10;


        final One2OneChannelInt[] productionsChannels = Channel.one2oneIntArray(producersNumber);
        final One2OneChannelInt[] requestChannels = Channel.one2oneIntArray(consumersNumber);
        final One2OneChannelInt[] consumptionChannels = Channel.one2oneIntArray(consumersNumber);

        final ChannelOutputInt[] productionsOut = Channel.getOutputArray(productionsChannels);
        final AltingChannelInputInt[] productionIn = Channel.getInputArray(productionsChannels);
        final AltingChannelInputInt[] reqIn = Channel.getInputArray(requestChannels);
        final ChannelOutputInt[] reqOut = Channel.getOutputArray(requestChannels);

        final ChannelOutputInt[] consumptionOut = Channel.getOutputArray(consumptionChannels);
        final ChannelInputInt[] consumptionIn = Channel.getInputArray(consumptionChannels);


        CSProcess[] actorsList = new CSProcess[productionsNumber + consumersNumber + 1];
        for (int i=0; i<producersNumber; i++) {
            actorsList[i] = new Producer(i, productionsOut[i], productionsNumber);
        }
        for (int i=0; i<consumersNumber; i++) {
            actorsList[i + producersNumber] = new Consumer(i, reqOut[i], consumptionIn[i]);
        }

        actorsList[consumersNumber + producersNumber] = new Buffer(bufferSize, productionIn, reqIn, consumptionOut);


        Parallel par = new Parallel(actorsList);
        par.run();

    }
}