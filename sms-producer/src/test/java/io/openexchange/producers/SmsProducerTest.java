package io.openexchange.producers;

import io.openexchange.pojos.Sms;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SmsProducerTest.class, SmsProducer.class})
@TestPropertySource(locations = "classpath:test.properties")
@EnableBinding(Sink.class)
public class SmsProducerTest {
    @InjectMocks
    private SmsProducer smsProducer;
    @MockBean
    private Source source;
    @MockBean
    @Qualifier(Source.OUTPUT)
    private MessageChannel channel;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(source.output()).thenReturn(channel);
        when(channel.send(Mockito.any())).thenReturn(true);
    }

    @Test(expected = javax.validation.ValidationException.class)
    public void validationFailed() throws Exception {
        smsProducer.send(new Sms());
    }

    @Test
    public void validationSuccess() throws Exception {
        smsProducer.send(new Sms()
                .withMessageId(UUID.randomUUID())
                .withMobileOriginate("1111")
                .withMobileTerminate("2222")
                .withReceiveTime(Date.from(Instant.now()))
                .withText("hello!"));
    }
}
