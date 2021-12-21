package actors;

import tracing.ActorState;

public abstract class Actor {
    ActorState actorState;
    Actor() {
        actorState = new ActorState();
    }

    public ActorState getActorState() {
        return actorState;
    }
}
