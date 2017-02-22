package io.openexchange.tx;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

class PseudoTransaction implements Cloneable {
    public enum State {
        NEW,
        BEGIN,
        COMMIT,
        ROLLBACK
    }

    private final UUID uuid = UUID.randomUUID();
    private final AtomicReference<State> state = new AtomicReference<>(State.NEW);

    UUID getUuid() {
        return uuid;
    }

    void setState(State state) {
        this.state.set(state);
    }

    State getState() {
        return state.get();
    }
}
