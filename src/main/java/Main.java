
import actors.BufferNet;
import actors.Consumer;
import actors.Producer;
import org.jcsp.lang.*;



public final class Main {
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
        final int bufferSize = 50;
        final int productionsNumber = 100;




        CSProcess[] producersList = new CSProcess[producersNumber];
        CSProcess[] consumersList = new CSProcess[consumersNumber];




        int[] layersSizes = new int[3];
        layersSizes[0] = producersNumber;
        layersSizes[1] = 4;
        layersSizes[2] = consumersNumber;

        BufferNet bufferNet = new BufferNet(layersSizes);

        for (int i=0; i<producersNumber; i++) {
            producersList[i] = new Producer(
                    i,
                    Channel.getInputArray(bufferNet.netReqOut)[0],
                    Channel.getOutputArray(bufferNet.netItemIn)[0],
                    productionsNumber);
        }
        for (int i=0; i<consumersNumber; i++) {
            consumersList[i] = new Consumer(
                    i,
                    Channel.getOutputArray(bufferNet.netReqIn)[0],
                    Channel.getInputArray(bufferNet.netItemOut)[0]
            );
        }


        Parallel producersPar = new Parallel(producersList);
        producersPar.run();
        Parallel consumersPar = new Parallel(consumersList);
        consumersPar.run();
        Parallel netActors = new Parallel(bufferNet.getActors());
        netActors.run();
    }
}