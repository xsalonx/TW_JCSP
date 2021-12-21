
import actors.Actor;
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

    private static final int delimitingLineLength = 50;
    private static final String logDelimitingLineStr = "_";
    private static final String sectionDelimitingLineStr = "-";

    static CSProcess[] producers;
    static CSProcess[] consumers;
    static BufferNet bufferNet;

    public static void main(String[] args) {
        final int producersNumber = 10;
        final int consumersNumber = 10;
        final int productionsNumber = 500;


        int[] layersSizes = new int[5];
        layersSizes[0] = producersNumber;
        layersSizes[1] = 1;
        layersSizes[2] = 4;
        layersSizes[3] = 4;
        layersSizes[4] = consumersNumber;

        bufferNet = new BufferNet(layersSizes);


        producers = new Producer[producersNumber];
        consumers = new Consumer[consumersNumber];

        ChannelInputInt[] productionReqInPC = Channel.getInputArray(bufferNet.netProductionReqPC);
        ChannelOutputInt[] itemProductionOutPC = Channel.getOutputArray(bufferNet.netItemProductionPC);
        for (int i=0; i<producersNumber; i++) {
            producers[i] = new Producer(
                    i,
                    productionReqInPC[i],
                    itemProductionOutPC[i],
                    productionsNumber,
                    0);
        }

        ChannelOutputInt[] consumptionReqOutPC = Channel.getOutputArray(bufferNet.netConsumptionReqPC);
        ChannelInputInt[] itemConsumptionInPC = Channel.getInputArray(bufferNet.netItemConsumptionPC);
        for (int i=0; i<consumersNumber; i++) {
            consumers[i] = new Consumer(
                    i,
                    consumptionReqOutPC[i],
                    itemConsumptionInPC[i],
                    1*(consumersNumber - i)/2
            );
        }

        CSProcess[] actors = concatWithStream(castActorsToCSProcess(consumers), castActorsToCSProcess(producers));
        actors = concatWithStream(bufferNet.getActors(), actors);
        Parallel parallel = new Parallel(actors);
        parallel.run();

        System.out.println(getStatistics());
    }






    static <T> T[] concatWithStream(T[] array1, T[] array2) {
        return Stream.concat(Arrays.stream(array1), Arrays.stream(array2))
                .toArray(size -> (T[]) Array.newInstance(array1.getClass().getComponentType(), size));
    }

    static <K> CSProcess[] castActorsToCSProcess(K[] array){
         CSProcess[] res = new CSProcess[array.length];
         for (int i=0; i<array.length; i++) {
             res[i] = (CSProcess) array[i];
         }
         return res;
    }

    static String toStringStatsFromActorsArray(Actor[] actors, String title) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(title).append(":");
        for (int i=0; i < actors.length; i++) {
            stringBuilder.append(",").append(i).append(":").append(actors[i].getActorState());
        }
        return stringBuilder.toString();
    }

    static String getStatistics() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(logDelimitingLineStr.repeat(delimitingLineLength)).append("\n");

        stringBuilder.append(toStringStatsFromActorsArray((Actor[]) producers, "producers")).append("\n");
        stringBuilder.append(sectionDelimitingLineStr.repeat(delimitingLineLength)).append("\n");
        stringBuilder.append(bufferNet.toStringNetStatistics()).append("\n");
        stringBuilder.append(sectionDelimitingLineStr.repeat(delimitingLineLength)).append("\n");
        stringBuilder.append(toStringStatsFromActorsArray((Actor[]) consumers, "consumers"));

        return stringBuilder.toString();
    }
}