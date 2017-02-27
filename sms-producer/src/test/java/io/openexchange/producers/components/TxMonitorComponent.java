package io.openexchange.producers.components;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TxMonitorComponent {
    private final AtomicInteger committedTxCounter = new AtomicInteger(0);
    private final AtomicInteger rollbackTxCounter = new AtomicInteger(0);

    public void registerCommit(){
        committedTxCounter.incrementAndGet();
    }

    public void registerRollback(){
        rollbackTxCounter.incrementAndGet();
    }

    public int countCommits(){
        return committedTxCounter.get();
    }

    public int countRollbacks(){
        return rollbackTxCounter.get();
    }

    public void reset(){
        committedTxCounter.lazySet(0);
        rollbackTxCounter.lazySet(0);
    }
}
