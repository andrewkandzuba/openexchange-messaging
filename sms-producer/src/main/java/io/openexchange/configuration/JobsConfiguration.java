package io.openexchange.configuration;

import io.openexchange.jobs.Job;
import io.openexchange.producers.SmsProducer;
import io.openexchange.pojos.Sms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Configuration
@RefreshScope
public class JobsConfiguration {
    @Value("${sms.outbound.queue.write.chunk.size:100}")
    private int smsWriteChunkSize;
    private final SmsProducer smsProducer;

    @Autowired
    public JobsConfiguration(SmsProducer smsProducer) {
        this.smsProducer = smsProducer;
    }

    @Job(parallelism = "${openexchange.sms.producer.job.parallelism:4}",
            repeatInterval = "${openexchange.sms.producer.job.repeatInterval:10}",
            repeatIntervalTimeUnit = "${openexchange.sms.producer.job.repeatIntervalTimeUnit:SECONDS}")
    public void jobSmsProducer() {
        smsProducer.send(new Sms()
                .withMessageId(UUID.randomUUID())
                .withMobileOriginate("+37258211717")
                .withMobileTerminate("+37258000000")
                .withText("Sample text")
                .withReceiveTime(Date.from(Instant.now())));
    }
}
