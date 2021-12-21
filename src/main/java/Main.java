
import actors.*;
import org.jcsp.lang.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;


public final class Main {



    private static final int delimitingLineLength = 50;
    private static final String logDelimitingLineStr = "_";
    private static final String sectionDelimitingLineStr = "-";

    static CSProcess[] producers;
    static CSProcess[] consumers;
    static BufferNet bufferNet;

    public static void main(String[] args) {
        final int producersNumber = Integer.parseInt(args[0]);
        final int consumersNumber = Integer.parseInt(args[1]);
        final int productionsNumber = Integer.parseInt(args[2]);


        int layersNumb = args.length - 1;
        int[] layersSizes = new int[layersNumb];
        layersSizes[0] = producersNumber;
        for (int i=1; i<layersNumb - 1; i++) {
            layersSizes[i] = Integer.parseInt(args[i + 2]);
        }
        layersSizes[layersNumb - 1] = consumersNumber;

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
                    0//1*(consumersNumber - i)/2
            );
        }

        CSProcess[] actors = concatWithStream(castActorsToCSProcess(consumers), castActorsToCSProcess(producers));
        actors = concatWithStream(bufferNet.getActors(), actors);
        Parallel parallel = new Parallel(actors);
        parallel.run();

        System.out.println(getStatistics().replaceAll("\u001B\\[[;\\d]*m", ""));
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
            stringBuilder.append(",").append(i).append(":")
                    .append(ConsoleColors.T_GREEN.v).append(actors[i].getActorState()).append(ConsoleColors.T_RESET.v);
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