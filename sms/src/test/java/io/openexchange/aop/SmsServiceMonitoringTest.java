package io.openexchange.aop;

import org.junit.Test;
import org.junit.runner.RunWith;
import io.openexchange.pojos.Sms;
import io.openexchange.sms.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmsServiceMonitoringTest.class)
@SpringBootApplication
@ImportAutoConfiguration(SmsServiceMonitoringTest.SmsServiceFactory.class)
public class SmsServiceMonitoringTest {
    @Autowired
    private SmsService smsService;

    @Test
    public void send() throws Exception {
        smsService.send(Collections.singleton(
                new Sms()
                        .withMessageId(UUID.randomUUID())
                        .withMobileOriginate("+37258000000")
                        .withMobileTerminate("+37258111111")
                        .withText("Test")
                        .withReceiveTime(Date.from(Instant.now()))));
    }

    @Test(expected = NullPointerException.class)
    public void sendingFails() throws Exception {
        smsService.send(Collections.singleton(
                new Sms()
                        .withMessageId(UUID.randomUUID())
                        .withMobileOriginate("+37258000000")
                        .withText("Test")
                        .withReceiveTime(Date.from(Instant.now()))));
    }

    @Test
    public void received() throws Exception {
        SmsServiceFactory.smsServiceMock.setInbox(Collections.singleton(
                new Sms()
                        .withMessageId(UUID.randomUUID())
                        .withMobileOriginate("+37258000000")
                        .withMobileTerminate("+37258111111")
                        .withText("Test")
                        .withReceiveTime(Date.from(Instant.now()))));
        smsService.receive(1);
    }

    @Test(expected = NullPointerException.class)
    public void receiptFails() throws Exception {
        SmsServiceFactory.smsServiceMock.setInbox(Collections.singleton(
                new Sms()
                        .withMessageId(UUID.randomUUID())
                        .withMobileOriginate("+37258000000")
                        .withText("Test")
                        .withReceiveTime(Date.from(Instant.now()))));
        smsService.receive(1);
    }

    @Configuration
    public static class SmsServiceFactory {
        static final SmsServiceMock smsServiceMock = new SmsServiceMock();

        @Bean
        public SmsService smsService(){
            return smsServiceMock;
        }
    }
}
