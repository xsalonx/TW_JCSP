package actors;

import com.github.rkumsher.collection.IterableUtils;
import org.jcsp.lang.*;

import java.util.HashSet;


public class Buffer extends Actor implements CSProcess {

    private final int layer;
    private final int index_;

    private final AltingChannelInputInt[] itemIn;
    private final AltingChannelInputInt[] reqIn;
    private final ChannelOutputInt[] itemOut;
    private final ChannelOutputInt[] reqOut;

    private final int shift;

    private int runningPredecessors;
    private int runningSuccessors;

    private Guard[] guards;
    private Alternative alt;

    private final int[] buffer;
    private final int bufferSize;
    int putIn = 0;
    int takeFrom = 0;


    public Buffer(int layer, int index, int size, ChannelOutputInt[] reqOut, AltingChannelInputInt[] itemIn,
                  AltingChannelInputInt[] reqIn, ChannelOutputInt[] itemOut) {
        super(0);

        assert reqIn.length == itemOut.length;

        this.layer = layer;
        this.index_ = index;

        this.buffer = new int[size];
        this.bufferSize = size;

        this.reqIn = reqIn;
        this.itemIn = itemIn;

        this.reqOut = reqOut;
        this.itemOut = itemOut;
        this.shift = itemIn.length;

        this.guards = getGuards();
        this.alt = new Alternative(guards);

        runningPredecessors = reqOut.length;
        runningSuccessors = itemOut.length;
    }

    private Guard[] getGuards() {
        Guard[] guards = new Guard[itemIn.length + reqIn.length];
        int i = 0;
        for (Guard g : itemIn) {
            guards[i] = g;
            i++;
        }
        for (Guard g : reqIn) {
            guards[i] = g;
            i++;
        }
        return guards;
    }

    private void sendInitReq() {
        for (int i=0; i<reqOut.length; i++) {
            reqOut[i].write(Codes.REQ.value);
        }
    }

    public void run() {

        sendInitReq();

        int index;
        while (runningSuccessors > 0 || runningPredecessors > 0) {
            index = alt.select();
            if (index < shift) {
                handlePredecessors(index);
            } else {
               handleSuccessors(index);
            }
        }
    }

    private void handlePredecessors(int index) {
        int item;
        if (putIn <= takeFrom + bufferSize) {
            item = itemIn[index].read();
            if (item == Codes.END.value)
                runningPredecessors--;

            else {

                buffer[putIn % buffer.length] = item;
                putIn++;
                reqOut[index].write(Codes.REQ.value);
            }
        } else if (runningSuccessors == 0) {
            //TODO send information to producer
        }
    }

    private void handleSuccessors(int index) {
        int item;
        if (takeFrom < putIn) {
            reqIn[index - shift].read();
            item = buffer[takeFrom % buffer.length];
            takeFrom++;
            itemOut[index - shift].write(item);
            this.actorState.incrementPassedItems();

        } else if (runningPredecessors == 0) {
            reqIn[index - shift].read();
            itemOut[index - shift].write(Codes.END.value);
            runningSuccessors--;
        }
    }
}