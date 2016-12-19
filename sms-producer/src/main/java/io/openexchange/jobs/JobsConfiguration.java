package io.openexchange.jobs;

import io.openexchange.pojos.Sms;
import io.openexchange.sms.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Configuration
@RefreshScope
public class JobsConfiguration {
    @Value("${sms.outbound.queue.write.chunk.size:100}")
    private int smsWriteChunkSize;
    private final SmsService smsService;

    @Autowired
    public JobsConfiguration(SmsService smsService) {
        this.smsService = smsService;
    }

    @Job(parallelism = "${openexchange.sms.producer.job.parallelism:4}", repeatIntervalTimeUnit = "${openexchange.sms.producer.job.repeatIntervalTimeUnit:MINUTES}")
    public void jobSmsProducer() {
        Set<Sms> messages = new HashSet<>();
        for (int i = 0; i < smsWriteChunkSize; i++) {
            messages.add(new Sms()
                    .withMessageId(UUID.randomUUID())
                    .withMobileOriginate("+37258211717")
                    .withMobileTerminate("+37258000000")
                    .withText("Sample text")
                    .withReceiveTime(Date.from(Instant.now())));
        }
        smsService.send(messages);
    }
}
