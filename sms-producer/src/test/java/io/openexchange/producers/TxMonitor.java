package io.openexchange.producers;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TxMonitor {
    private final AtomicInteger committedTxCounter = new AtomicInteger(0);
    private final AtomicInteger rolledBackTxCounter = new AtomicInteger(0);

    void registerCommit(){
        committedTxCounter.incrementAndGet();
    }

    void registerRollback(){
        rolledBackTxCounter.incrementAndGet();
    }

    int getCommitsCounter(){
        return committedTxCounter.get();
    }

    int getRollbackCounter(){
        return rolledBackTxCounter.get();
    }
}
