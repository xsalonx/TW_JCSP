package actors;

import tracing.ActorState;

public abstract class Actor {

    public enum Codes{
        END(-1),
        NORESORCES(-2),
        REQ(0);

        private Codes(int value) {
            this.value = value;
        }


        final int value;

    }

    ActorState actorState;
    int delay;
    Actor(int delay) {
        actorState = new ActorState();
        this.delay = delay;
    }

    void sleepActor() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ActorState getActorState() {
        return actorState;
    }
}
