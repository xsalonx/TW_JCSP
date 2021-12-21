package actors;

import com.sun.tools.javac.Main;
import org.jcsp.lang.*;


public class BufferNet {

    private final static int BUFFERS_SIZE = 10;


    private Buffer[][] buffersLayers;
    private Connector[][] connectorsLayers;

    public One2OneChannelInt[] firstReqPC;
    public ChannelOutputInt[] firstReqOutputPC;
    public One2OneChannelInt[] firstItemPC;
    public ChannelInputInt[] firstItemInputPC;

    // item passing
    public One2OneChannelInt[][] leftItemPC;
    public One2OneChannelInt[][] rightItemPC;

    public AltingChannelInputInt[][] leftItemInputPC;
    public ChannelOutputInt[][] leftItemOutputPC;
    public AltingChannelInputInt[][] rightItemInputPC;
    public ChannelOutputInt[][] rightItemOutputPC;

    // req passing
    public One2OneChannelInt[][] leftReqPC;
    public One2OneChannelInt[][] rightReqPC;

    public AltingChannelInputInt[][] leftReqInputPC;
    public ChannelOutputInt[][] leftReqOutputPC;
    public AltingChannelInputInt[][] rightReqInputPC;
    public ChannelOutputInt[][] rightReqOutputPC;


    public One2OneChannelInt[] netItemProductionPC;
    public One2OneChannelInt[] netProductionReqPC;
    public One2OneChannelInt[] netItemConsumptionPC;
    public One2OneChannelInt[] netConsumptionReqPC;

    private int[] layersSizes;
    private int layersNumb;

    private void initChannels(int[] layersSizes_) {
        layersSizes = layersSizes_;
        layersNumb = layersSizes.length;

        buffersLayers = new Buffer[layersNumb][];
        connectorsLayers = new Connector[layersNumb][];

        buffersLayers[0] = new Buffer[layersSizes[0]];
        connectorsLayers[0] = new Connector[layersSizes[0]];
        for (int i = 1; i < layersNumb; i++) {
            buffersLayers[i] = new Buffer[layersSizes[i]];
            connectorsLayers[i] = new Connector[layersSizes[i - 1] * layersSizes[i]];
        }

        leftItemPC = new One2OneChannelInt[layersNumb][];
        rightItemPC = new One2OneChannelInt[layersNumb][];

        leftItemInputPC = new AltingChannelInputInt[layersNumb][];
        leftItemOutputPC = new ChannelOutputInt[layersNumb][];
        rightItemInputPC = new AltingChannelInputInt[layersNumb][];
        rightItemOutputPC = new ChannelOutputInt[layersNumb][];


        leftReqPC = new One2OneChannelInt[layersNumb][];
        rightReqPC = new One2OneChannelInt[layersNumb][];

        leftReqInputPC = new AltingChannelInputInt[layersNumb][];
        leftReqOutputPC = new ChannelOutputInt[layersNumb][];
        rightReqInputPC = new AltingChannelInputInt[layersNumb][];
        rightReqOutputPC = new ChannelOutputInt[layersNumb][];


        // left item passing
        leftItemPC[0] = Channel.one2oneIntArray(layersSizes[0]);
        leftItemOutputPC[0] = Channel.getOutputArray(leftItemPC[0]);
        leftItemInputPC[0] = Channel.getInputArray(leftItemPC[0]);

        leftReqPC[0] = Channel.one2oneIntArray(layersSizes[0]);
        leftReqOutputPC[0] = Channel.getOutputArray(leftReqPC[0]);
        leftReqInputPC[0] = Channel.getInputArray(leftReqPC[0]);

        for (int i = 1; i < layersNumb; i++) {
            leftItemPC[i] = Channel.one2oneIntArray(layersSizes[i - 1] * layersSizes[i]);
            leftItemOutputPC[i] = Channel.getOutputArray(leftItemPC[i]);
            leftItemInputPC[i] = Channel.getInputArray(leftItemPC[i]);

            leftReqPC[i] = Channel.one2oneIntArray(layersSizes[i - 1] * layersSizes[i]);
            leftReqOutputPC[i] = Channel.getOutputArray(leftReqPC[i]);
            leftReqInputPC[i] = Channel.getInputArray(leftReqPC[i]);
        }


        // right item passing
        for (int i = 0; i < layersNumb - 1; i++) {
            rightItemPC[i] = Channel.one2oneIntArray(layersSizes[i] * layersSizes[i + 1]);
            rightItemOutputPC[i] = Channel.getOutputArray(rightItemPC[i]);
            rightItemInputPC[i] = Channel.getInputArray(rightItemPC[i]);

            rightReqPC[i] = Channel.one2oneIntArray(layersSizes[i] * layersSizes[i + 1]);
            rightReqOutputPC[i] = Channel.getOutputArray(rightReqPC[i]);
            rightReqInputPC[i] = Channel.getInputArray(rightReqPC[i]);
        }

        rightItemPC[layersNumb - 1] = Channel.one2oneIntArray(layersSizes[layersNumb - 1]);
        rightItemOutputPC[layersNumb - 1] = Channel.getOutputArray(rightItemPC[layersNumb - 1]);
        rightItemInputPC[layersNumb - 1] = Channel.getInputArray(rightItemPC[layersNumb - 1]);

        rightReqPC[layersNumb - 1] = Channel.one2oneIntArray(layersSizes[layersNumb - 1]);
        rightReqOutputPC[layersNumb - 1] = Channel.getOutputArray(rightReqPC[layersNumb - 1]);
        rightReqInputPC[layersNumb - 1] = Channel.getInputArray(rightReqPC[layersNumb - 1]);



        firstReqPC = Channel.one2oneIntArray(layersSizes[0]);
        firstReqOutputPC = Channel.getOutputArray(firstReqPC);
        firstItemPC = Channel.one2oneIntArray(layersSizes[0]);
        firstItemInputPC = Channel.getInputArray(firstItemPC);

    }

    private void initBuffersLayers() {

        int firstLayerIndex = 0;
        int lastLayerIndex = layersNumb - 1;


        int n_prev;
        int n;
        int n_next;

        // first buffer layer
        n = layersSizes[firstLayerIndex];
        n_next = layersSizes[firstLayerIndex + 1];
        for (int b1 = 0; b1 < n; b1++) {
            ChannelOutputInt[] localLeftReqOutput = new ChannelOutputInt[1];
            AltingChannelInputInt[] localLeftItemInput = new AltingChannelInputInt[1];
            localLeftReqOutput[0] = leftReqOutputPC[0][b1];
            localLeftItemInput[0] = leftItemInputPC[0][b1];

            AltingChannelInputInt[] localRightReqInput = new AltingChannelInputInt[n_next];
            ChannelOutputInt[] localRightItemOutput = new ChannelOutputInt[n_next];
            for (int b2 = 0; b2 < n_next; b2++) {
                localRightReqInput[b2] = rightReqInputPC[0][n * b2 + b1];
                localRightItemOutput[b2] = rightItemOutputPC[0][n * b2 + b1];
            }

            buffersLayers[0][b1] = new Buffer(firstLayerIndex, b1, BUFFERS_SIZE, localLeftReqOutput, localLeftItemInput, localRightReqInput, localRightItemOutput);
        }


        // buffers
        for (int layerIndex = 1; layerIndex < layersNumb - 1; layerIndex++) {
            n_prev = layersSizes[layerIndex - 1];
            n = layersSizes[layerIndex];
            n_next = layersSizes[layerIndex + 1];

            for (int b = 0; b < n; b++) {
                ChannelOutputInt[] localLeftReqOutput = new ChannelOutputInt[n_prev];
                AltingChannelInputInt[] localLeftItemInput = new AltingChannelInputInt[n_prev];
                for (int b_1 = 0; b_1 < n_prev; b_1++) {
                    localLeftReqOutput[b_1] = leftReqOutputPC[layerIndex][n_prev * b + b_1];
                    localLeftItemInput[b_1] = leftItemInputPC[layerIndex][n_prev * b + b_1];
                }

                AltingChannelInputInt[] localRightReqInput = new AltingChannelInputInt[n_next];
                ChannelOutputInt[] localRightItemOutput = new ChannelOutputInt[n_next];
                for (int b1 = 0; b1 < n_next; b1++) {
                    localRightReqInput[b1] = rightReqInputPC[layerIndex][n * b1 + b];
                    localRightItemOutput[b1] = rightItemOutputPC[layerIndex][n * b1 + b];
                }

                buffersLayers[layerIndex][b] = new Buffer(layerIndex, b, BUFFERS_SIZE, localLeftReqOutput, localLeftItemInput, localRightReqInput, localRightItemOutput);
            }
        }

        //last buffer layer
        n_prev = layersSizes[lastLayerIndex - 1];
        n = layersSizes[lastLayerIndex];

        for (int b = 0; b < n; b++) {
            ChannelOutputInt[] localLeftReqOutput = new ChannelOutputInt[n_prev];
            AltingChannelInputInt[] localLeftItemInput = new AltingChannelInputInt[n_prev];
            for (int b_1 = 0; b_1 < n_prev; b_1++) {
                localLeftReqOutput[b_1] = leftReqOutputPC[lastLayerIndex][n_prev * b + b_1];
                localLeftItemInput[b_1] = leftItemInputPC[lastLayerIndex][n_prev * b + b_1];
            }

            AltingChannelInputInt[] localRightReqInput = new AltingChannelInputInt[1];
            ChannelOutputInt[] localRightItemOutput = new ChannelOutputInt[1];
            localRightReqInput[0] = rightReqInputPC[lastLayerIndex][b];
            localRightItemOutput[0] = rightItemOutputPC[lastLayerIndex][b];

            buffersLayers[lastLayerIndex][b] = new Buffer(lastLayerIndex, b, BUFFERS_SIZE, localLeftReqOutput, localLeftItemInput, localRightReqInput, localRightItemOutput);
        }
    }

    private void initConnectors() {


        for (int c = 0; c < layersSizes[0]; c++) {
            connectorsLayers[0][c] = new Connector(
                    firstReqOutputPC[c],
                    firstItemInputPC[c],
                    leftReqInputPC[0][c],
                    leftItemOutputPC[0][c]);
        }

        // connector Construction
        for (int i = 1; i < layersNumb; i++) {
            int n_prev = layersSizes[i - 1];
            int n = layersSizes[i];

            for (int b_1 = 0; b_1 < n_prev; b_1++) {
                for (int b = 0; b < n; b++) {
                    connectorsLayers[i][n_prev * b + b_1] = new Connector(
                            rightReqOutputPC[i - 1][n_prev * b + b_1],
                            rightItemInputPC[i - 1][n_prev * b + b_1],
                            leftReqInputPC[i][n_prev * b + b_1],
                            leftItemOutputPC[i][n_prev * b + b_1]);
                }
            }
        }

        netItemProductionPC = firstItemPC;
        netProductionReqPC = firstReqPC;

        netItemConsumptionPC = rightItemPC[layersNumb - 1];
        netConsumptionReqPC = rightReqPC[layersNumb - 1];
    }

    public BufferNet(int[] layersSizes_) {
        initChannels(layersSizes_);
        initBuffersLayers();
        initConnectors();
    }

    public CSProcess[] getActors() {
        int count = 0;
        for (int i = 0; i < buffersLayers.length; i++) {
            count += buffersLayers[i].length;
            count += connectorsLayers[i].length;
        }

        CSProcess[] netActors = new CSProcess[count];
        int j = 0;

        for (Connector[] connectorsLayer : connectorsLayers) {
            for (Connector connector : connectorsLayer) {
                netActors[j] = connector;
                j++;
            }
        }

        for (Buffer[] buffersLayer : buffersLayers) {
            for (Buffer buffer : buffersLayer) {
                netActors[j] = buffer;
                j++;
            }
        }

        return netActors;
    }

    public String toStringNetStatistics() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int layerIndex = 0; layerIndex < layersNumb - 1; layerIndex ++) {
            stringBuilder.append(toStringLayer(layerIndex));
            stringBuilder.append("\n");
        }
        stringBuilder.append(toStringLayer(layersNumb - 1));

        return stringBuilder.toString();
    }

    private String toStringLayer(int layerIndex) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("layer:").append(layerIndex);
        for (int b=0; b < layersSizes[layerIndex]; b++) {
            stringBuilder.append(",").append(b).append(":")
                    .append(ConsoleColors.T_BLUE.v).append(buffersLayers[layerIndex][b].actorState).append(ConsoleColors.T_RESET.v);
        }
        return stringBuilder.toString();
    }
}
