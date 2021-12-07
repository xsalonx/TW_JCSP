package actors;

import org.jcsp.lang.*;

public class BufferNet {

    private final Buffer[][] buffersLayers;
    private final BuffersConnector[][] connectorsLayers;

    public final One2OneChannelInt[][] leftItemPassingChannels;
    public final One2OneChannelInt[][] rightItemPassingChannels;

    public final One2OneChannelInt[] firstReqPassingChannels;
    public final ChannelOutputInt[] rightFirstReqPassingChannels;
    public final One2OneChannelInt[] firstItemPassingChannels;
    public final ChannelInputInt[] rightFirstItemPassingChannels;

    public final AltingChannelInputInt[][] leftItemPassingChannelsInput;
    public final ChannelOutputInt[][] leftItemPassingChannelsOutput;
    public final AltingChannelInputInt[][] rightItemPassingChannelsInput;
    public final ChannelOutputInt[][] rightItemPassingChannelsOutput;


    public final One2OneChannelInt[][] leftReqPassingChannels;
    public final One2OneChannelInt[][] rightReqPassingChannels;

    public final AltingChannelInputInt[][] leftReqPassingChannelsInput;
    public final ChannelOutputInt[][] leftReqPassingChannelsOutput;
    public final AltingChannelInputInt[][] rightReqPassingChannelsInput;
    public final ChannelOutputInt[][] rightReqPassingChannelsOutput;

    public final One2OneChannelInt[] netItemIn;
    public final One2OneChannelInt[] netReqOut;
    public final One2OneChannelInt[] netItemOut;
    public final One2OneChannelInt[] netReqIn;

    public BufferNet(int[] layersSizes) {

        int layersNumb = layersSizes.length;

        buffersLayers = new Buffer[layersNumb][];
        connectorsLayers = new BuffersConnector[layersNumb][];

        buffersLayers[0] = new Buffer[layersSizes[0]];
        connectorsLayers[0] = new BuffersConnector[layersSizes[0]];
        for (int i = 1; i < layersNumb; i++) {
            buffersLayers[i] = new Buffer[layersSizes[i]];
            connectorsLayers[i] = new BuffersConnector[layersSizes[i - 1] * layersSizes[i]];
        }

        leftItemPassingChannels = new One2OneChannelInt[layersNumb][];
        rightItemPassingChannels = new One2OneChannelInt[layersNumb][];

        leftItemPassingChannelsInput = new AltingChannelInputInt[layersNumb][];
        leftItemPassingChannelsOutput = new ChannelOutputInt[layersNumb][];
        rightItemPassingChannelsInput = new AltingChannelInputInt[layersNumb][];
        rightItemPassingChannelsOutput = new ChannelOutputInt[layersNumb][];


        leftReqPassingChannels = new One2OneChannelInt[layersNumb][];
        rightReqPassingChannels = new One2OneChannelInt[layersNumb][];

        leftReqPassingChannelsInput = new AltingChannelInputInt[layersNumb][];
        leftReqPassingChannelsOutput = new ChannelOutputInt[layersNumb][];
        rightReqPassingChannelsInput = new AltingChannelInputInt[layersNumb][];
        rightReqPassingChannelsOutput = new ChannelOutputInt[layersNumb][];




        // left item passing
        leftItemPassingChannels[0] = Channel.one2oneIntArray(layersSizes[0]);
        leftItemPassingChannelsOutput[0] = Channel.getOutputArray(leftItemPassingChannels[0]);
        leftItemPassingChannelsInput[0] = Channel.getInputArray(leftItemPassingChannels[0]);

        leftReqPassingChannels[0] = Channel.one2oneIntArray(layersSizes[0]);
        leftReqPassingChannelsOutput[0] = Channel.getOutputArray(leftReqPassingChannels[0]);
        leftReqPassingChannelsInput[0] = Channel.getInputArray(leftReqPassingChannels[0]);
        for (int i=1; i < layersNumb; i++) {
            leftItemPassingChannels[i] = Channel.one2oneIntArray(layersSizes[i - 1] * layersSizes[i]);
            leftItemPassingChannelsOutput[i] = Channel.getOutputArray(leftItemPassingChannels[i]);
            leftItemPassingChannelsInput[i] = Channel.getInputArray(leftItemPassingChannels[i]);

            leftReqPassingChannels[i] = Channel.one2oneIntArray(layersSizes[i - 1] * layersSizes[i]);
            leftReqPassingChannelsOutput[i] = Channel.getOutputArray(leftReqPassingChannels[i]);
            leftReqPassingChannelsInput[i] = Channel.getInputArray(leftReqPassingChannels[i]);
        }


        // right item passing
        for (int i=0; i < layersNumb - 1; i++) {
            rightItemPassingChannels[i] = Channel.one2oneIntArray(layersSizes[i] * layersSizes[i + 1]);
            rightItemPassingChannelsOutput[i] = Channel.getOutputArray(rightItemPassingChannels[i]);
            rightItemPassingChannelsInput[i] = Channel.getInputArray(rightItemPassingChannels[i]);

            rightReqPassingChannels[i] = Channel.one2oneIntArray(layersSizes[i] * layersSizes[i + 1]);
            rightReqPassingChannelsOutput[i] = Channel.getOutputArray(rightItemPassingChannels[i]);
            rightReqPassingChannelsInput[i] = Channel.getInputArray(rightItemPassingChannels[i]);
        }
        rightItemPassingChannels[layersNumb - 1] = Channel.one2oneIntArray(layersSizes[layersNumb - 1]);
        rightItemPassingChannelsOutput[layersNumb - 1] = Channel.getOutputArray(rightItemPassingChannels[layersNumb - 1]);
        rightItemPassingChannelsInput[layersNumb - 1] = Channel.getInputArray(rightItemPassingChannels[layersNumb - 1]);

        rightReqPassingChannels[layersNumb - 1] = Channel.one2oneIntArray(layersSizes[layersNumb - 1]);
        rightReqPassingChannelsOutput[layersNumb - 1] = Channel.getOutputArray(rightItemPassingChannels[layersNumb - 1]);
        rightReqPassingChannelsInput[layersNumb - 1] = Channel.getInputArray(rightItemPassingChannels[layersNumb - 1]);


        // first buffer layer
        for (int b=0; b < layersSizes[0]; b++) {
            ChannelOutputInt[] localLeftReqOutput = new ChannelOutputInt[1];
            AltingChannelInputInt[] localLeftItemInput = new AltingChannelInputInt[1];
            localLeftReqOutput[0] = leftReqPassingChannelsOutput[0][b];
            localLeftItemInput[0] = leftItemPassingChannelsInput[0][b];


            AltingChannelInputInt[] localRightReqInput = new AltingChannelInputInt[layersSizes[1]];
            ChannelOutputInt[] localRightItemOutput = new ChannelOutputInt[layersSizes[1]];
            for (int k=0; k<layersSizes[1]; k++) {
                localRightReqInput[k] = rightReqPassingChannelsInput[0][layersSizes[0]*k + b];
                localRightItemOutput[k] = rightItemPassingChannelsOutput[0][layersSizes[0]*k + b];
            }

            buffersLayers[0][b] = new Buffer(10, localLeftReqOutput, localLeftItemInput, localRightReqInput, localRightItemOutput);
        }


        // buffers
        for (int i=1; i<layersNumb - 1; i++) {
            for (int b=0; b < layersSizes[i]; b++) {
                ChannelOutputInt[] localLeftReqOutput = new ChannelOutputInt[layersSizes[i - 1]];
                AltingChannelInputInt[] localLeftItemInput = new AltingChannelInputInt[layersSizes[i - 1]];
                for (int k=0; k<layersSizes[i - 1]; k++) {
                    localLeftReqOutput[k] = leftReqPassingChannelsOutput[i][layersSizes[i-1]*b + k];
                    localLeftItemInput[k] = leftItemPassingChannelsInput[i][layersSizes[i-1]*b + k];
                }

                AltingChannelInputInt[] localRightReqInput = new AltingChannelInputInt[layersSizes[i + 1]];
                ChannelOutputInt[] localRightItemOutput = new ChannelOutputInt[layersSizes[i + 1]];
                for (int k=0; k<layersSizes[i + 1]; k++) {
                    localRightReqInput[k] = rightReqPassingChannelsInput[i][layersSizes[i]*k + b];
                    localRightItemOutput[k] = rightItemPassingChannelsOutput[i][layersSizes[i]*k + b];
                }

                buffersLayers[i][b] = new Buffer(10, localLeftReqOutput, localLeftItemInput, localRightReqInput, localRightItemOutput);
            }
        }

        //last buffer layer
        // first buffer layer
        int i = layersNumb - 1;
        for (int b=0; b < layersSizes[layersNumb - 1]; b++) {
            ChannelOutputInt[] localLeftReqOutput = new ChannelOutputInt[layersSizes[i - 1]];
            AltingChannelInputInt[] localLeftItemInput = new AltingChannelInputInt[layersSizes[i - 1]];
            for (int k=0; k<layersSizes[i - 1]; k++) {
                localLeftReqOutput[i] = leftReqPassingChannelsOutput[i][layersSizes[i-1]*b + k];
                localLeftItemInput[i] = leftItemPassingChannelsInput[i][layersSizes[i-1]*b + k];
            }
            AltingChannelInputInt[] localRightReqInput = new AltingChannelInputInt[layersSizes[i]];
            ChannelOutputInt[] localRightItemOutput = new ChannelOutputInt[layersSizes[i]];
            for (int k=0; k<layersSizes[i]; k++) {
                localRightReqInput[k] = rightReqPassingChannelsInput[i][k];
                localRightItemOutput[k] = rightItemPassingChannelsOutput[i][k];
            }
            buffersLayers[0][b] = new Buffer(10, localLeftReqOutput, localLeftItemInput, localRightReqInput, localRightItemOutput);
        }





        firstReqPassingChannels = Channel.one2oneIntArray(layersSizes[0]);
        rightFirstReqPassingChannels = Channel.getOutputArray(firstReqPassingChannels);
        firstItemPassingChannels = Channel.one2oneIntArray(layersSizes[0]);
        rightFirstItemPassingChannels = Channel.getInputArray(firstItemPassingChannels);

        for (int c=0; c<layersSizes[0]; c++) {
            connectorsLayers[0][c] = new BuffersConnector(0, 0,
                    rightFirstItemPassingChannels[c],
                    leftItemPassingChannelsOutput[0][c],
                    rightFirstReqPassingChannels[c],
                    leftReqPassingChannelsInput[0][c]);
        }

        // connector Construction
        for (int j=1; j<layersNumb; j++) {
            int n1 = layersSizes[j-1];
            int n2 = layersSizes[j];
            for (int b1 = 0; b1 < n1; b1++) {
                for (int b2=0; b2 < n2; b2++) {
                    connectorsLayers[j][n1 * b2 + b1] = new BuffersConnector(0, 0,
                            leftItemPassingChannelsInput[j - 1][n1 * b2 + b1],
                            rightItemPassingChannelsOutput[j][n1 * b2 + b1],
                            leftReqPassingChannelsOutput[j - 1][n1 * b2 + b1],
                            rightReqPassingChannelsInput[j][n1 * b2 + b1]);
                }
            }
        }

        netItemIn = firstItemPassingChannels;
        netItemOut = rightItemPassingChannels[layersNumb - 1];
        netReqOut = firstReqPassingChannels;
        netReqIn = rightReqPassingChannels[layersNumb - 1];
    }

    public CSProcess[] getActors() {
        int count = 0;
        for (int i=0; i< buffersLayers.length; i++) {
            count += buffersLayers[i].length;
            count += connectorsLayers[i].length;
        }

        CSProcess[] netActors = new CSProcess[count];
        int j=0;
        for (int i=0; i< buffersLayers.length; i++) {
            for (int k=0; k<buffersLayers[i].length; k++){
                netActors[j] = buffersLayers[i][k];
                j++;
            }
            for (int k=0; k< connectorsLayers[i].length; k++){
                netActors[j] = connectorsLayers[i][k];
                j++;
            }
        }

        return netActors;
    }
}
