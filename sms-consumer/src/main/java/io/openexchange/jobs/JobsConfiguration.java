package io.openexchange.jobs;

import io.openexchange.jpa.SmsEntity;
import io.openexchange.jpa.SmsRepository;
import io.openexchange.pojos.Sms;
import io.openexchange.sms.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@Configuration
@RefreshScope
public class JobsConfiguration {
    @Value("${sms.outbound.queue.read.chunk.size:100}")
    private int smsReadChunkSize;
    private final SmsService smsService;
    private final SmsRepository smsRepository;

    @Autowired
    public JobsConfiguration(SmsService smsService, SmsRepository smsRepository) {
        this.smsService = smsService;
        this.smsRepository = smsRepository;
    }

    @Job(parallelism = "${openexchange.sms.consumer.job.parallelism:4}", repeatIntervalTimeUnit = "${openexchange.sms.consumer.job.repeatIntervalTimeUnit:MINUTES}")
    public void jobSmsConsumer() {
        while (!Thread.currentThread().isInterrupted()) {
            consume();
        }
    }

    @Transactional(rollbackFor = Throwable.class, timeout = 5)
    private void consume() {
        Collection<Sms> messages = smsService.receive(smsReadChunkSize);
        if (messages.isEmpty()) {
            return;
        }
        Collection<SmsEntity> entities = new HashSet<>();
        messages.stream().map(this::transform).collect(Collectors.toCollection(() -> entities));
        smsRepository.save(entities);
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
