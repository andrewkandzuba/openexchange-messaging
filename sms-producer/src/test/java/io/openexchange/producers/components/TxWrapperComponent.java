package io.openexchange.producers.components;

import io.openexchange.pojos.Sms;
import io.openexchange.producers.ProduceException;
import io.openexchange.producers.SmsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.Queue;

@Component
public class TxWrapperComponent {
    @Autowired
    private SmsProducer smsProducer;
    private final Queue<Sms> partition = new LinkedList<>();

    @Transactional(rollbackFor = Throwable.class)
    public void sendOneWaiting() throws ProduceException {
        synchronized (partition) {
            Sms sms = partition.peek();
            if (sms != null) {
                // ToDo: send should be timed and followed by monitor release to avoid live block.
                smsProducer.send(sms);
                partition.remove();
            }
        }
    }

    public TxWrapperComponent schedule(Sms sms){
        synchronized (partition) {
            partition.offer(sms);
        }
        return this;
    }

    public Sms peek(){
        synchronized (partition){
            return partition.peek();
        }
    }

    public void forceFlush(){
        synchronized (partition){
            partition.clear();
        }
    }
}
