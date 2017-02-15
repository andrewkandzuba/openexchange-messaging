package io.openexchange.consumers;

import io.openexchange.jpa.SmsEntity;
import io.openexchange.jpa.SmsRepository;
import io.openexchange.pojos.Sms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

@Component
@EnableBinding(Sink.class)
public class SmsConsumer {
    private final SmsRepository smsRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Autowired
    public SmsConsumer(SmsRepository smsRepository, PlatformTransactionManager platformTransactionManager) {
        this.smsRepository = smsRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Transactional(rollbackFor = Throwable.class, timeout = 5)
    @StreamListener(Sink.INPUT)
    private void consume(@Valid Sms sms) {
        smsRepository.save(transform(sms));
    }

    private SmsEntity transform(Sms sms) {
        return new SmsEntity(
                sms.getMessageId().toString(),
                sms.getMobileOriginate(),
                sms.getMobileTerminate(),
                sms.getText(),
                sms.getReceiveTime());
    }
}
