package tracing;

public class ActorState {
    int passedItems;

    public ActorState() {
        passedItems = 0;
    }
    public void incrementPassedItems() {
        passedItems++;
    }

    public int getPassedItems() {
        return passedItems;
    }

    @Override
    public String toString() {
        return "" + passedItems;
    }
}
