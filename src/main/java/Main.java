
import actors.BufferNet;
import actors.Consumer;
import actors.Producer;
import org.jcsp.lang.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;


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
        final int productionsNumber = 50;


        CSProcess[] producersList = new CSProcess[producersNumber];
        CSProcess[] consumersList = new CSProcess[consumersNumber];


        int[] layersSizes = new int[4];
        layersSizes[0] = producersNumber;
        layersSizes[1] = 10;
        layersSizes[2] = 10;
        layersSizes[3] = consumersNumber;

        BufferNet bufferNet = new BufferNet(layersSizes);

        for (int i=0; i<producersNumber; i++) {
            producersList[i] = new Producer(
                    i,
                    Channel.getInputArray(bufferNet.netReqOutPC)[i],
                    Channel.getOutputArray(bufferNet.netItemInPC)[i],
                    productionsNumber);
        }
        for (int i=0; i<consumersNumber; i++) {
            consumersList[i] = new Consumer(
                    i,
                    Channel.getOutputArray(bufferNet.netReqInPC)[i],
                    Channel.getInputArray(bufferNet.netItemOutPC)[i]
            );
        }

        CSProcess[] actors = concatWithStream(producersList, consumersList);
        actors = concatWithStream(actors, bufferNet.getActors());
        Parallel parallel = new Parallel(actors);
        parallel.run();
    }

    static <T> T[] concatWithStream(T[] array1, T[] array2) {
        return Stream.concat(Arrays.stream(array1), Arrays.stream(array2))
                .toArray(size -> (T[]) Array.newInstance(array1.getClass().getComponentType(), size));
    }
}