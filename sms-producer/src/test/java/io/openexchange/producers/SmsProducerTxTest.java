package io.openexchange.producers;

import io.openexchange.pojos.Sms;
import io.openexchange.tx.MonitorablePseudoTransactionManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        SmsProducerTxTest.class,
        SmsProducerTxTest.TestApplication.class,
        SmsProducer.class
})
@DirtiesContext
@TestPropertySource(locations = {
        "classpath:test.properties",
        "classpath:test.binders.properties"
})
public class SmsProducerTxTest {
    @MockBean
    private Source source;
    @MockBean
    @Qualifier(Source.OUTPUT)
    private MessageChannel channel;
    @SpyBean
    private SmsProducer smsProducer;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        assertTrue(platformTransactionManager instanceof MonitorablePseudoTransactionManager);
    }

    @Test
    public void transactionCommit() throws Exception {
        when(source.output()).thenReturn(channel);
        when(channel.send(Mockito.any())).thenReturn(true);
        smsProducer.send(new Sms()
                .withMessageId(UUID.randomUUID())
                .withMobileOriginate("11111111")
                .withMobileTerminate("22222222")
                .withReceiveTime(Date.from(Instant.now()))
                .withText("hello!"));
        assertTrue(((MonitorablePseudoTransactionManager)platformTransactionManager).transactions().count() > 0);
    }

    @SpringBootApplication
    @EnableBinding({Source.class, Sink.class})
    @EnableTransactionManagement(proxyTargetClass = true)
    public static class TestApplication {
        @Bean
        public PlatformTransactionManager platformTransactionManager(){
            return new MonitorablePseudoTransactionManager();
        }
    }
}
