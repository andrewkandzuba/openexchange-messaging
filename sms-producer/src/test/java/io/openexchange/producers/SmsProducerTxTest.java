package io.openexchange.producers;

import io.openexchange.pojos.Sms;
import io.openexchange.producers.components.TxMonitorComponent;
import io.openexchange.producers.components.TxWrapperComponent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.SimpleTransactionStatus;

import javax.inject.Inject;
import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        SmsProducerTxTest.class,
        SmsProducerTxTest.TestApplication.class,
        SmsProducer.class,
        TxMonitorComponent.class,
        TxWrapperComponent.class
})
@DirtiesContext(methodMode = BEFORE_METHOD)
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
    @Inject
    private TxMonitorComponent txMonitorComponent;
    @Inject
    private TxWrapperComponent txWrapperComponent;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        txWrapperComponent.forceFlush();
        txMonitorComponent.reset();
    }

    @Test
    public void transactionCommit() throws Exception {
        when(source.output()).thenReturn(channel);
        when(channel.send(Mockito.any())).thenReturn(true);
        smsProducer.send(create());
        assertThat(txMonitorComponent.countCommits()).isEqualTo(1);
        assertThat(txMonitorComponent.countRollbacks()).isEqualTo(0);
    }

    @Test
    public void transactionRollback() throws Exception {
        when(source.output()).thenReturn(channel);
        when(channel.send(Mockito.any())).thenReturn(false);
        assertThatThrownBy(() -> smsProducer.send(create())).isInstanceOf(ProduceException.class);
        assertThat(txMonitorComponent.countCommits()).isEqualTo(0);
        assertThat(txMonitorComponent.countRollbacks()).isEqualTo(1);
    }

    @Test
    public void propagateSuccess() throws Exception {
        when(source.output()).thenReturn(channel);
        when(channel.send(Mockito.any())).thenReturn(true);
        txWrapperComponent.schedule(create()).sendOneWaiting();
        assertThat(txMonitorComponent.countCommits()).isEqualTo(2);
        assertThat(txMonitorComponent.countRollbacks()).isEqualTo(0);
        assertThat(txWrapperComponent.peek()).isNull();
    }

    @Test
    public void propagateRollback() throws Exception {
        when(source.output()).thenReturn(channel);
        when(channel.send(Mockito.any())).thenReturn(false);
        assertThatThrownBy(() -> txWrapperComponent.schedule(create()).sendOneWaiting()).isInstanceOf(ProduceException.class);
        assertThat(txMonitorComponent.countCommits()).isEqualTo(0);
        assertThat(txMonitorComponent.countRollbacks()).isEqualTo(2);
        assertThat(txWrapperComponent.peek()).isNotNull();
    }

    private static Sms create() {
        return new Sms()
                .withMessageId(UUID.randomUUID())
                .withMobileOriginate("11111111")
                .withMobileTerminate("22222222")
                .withReceiveTime(Date.from(Instant.now()))
                .withText("hello!");
    }

    @SpringBootApplication
    @EnableBinding({Source.class, Sink.class})
    @EnableTransactionManagement(proxyTargetClass = true)
    public static class TestApplication {
        @Inject
        private TxMonitorComponent txMonitorComponent;

        @Bean
        public PlatformTransactionManager platformTransactionManager() {
            return new PlatformTransactionManager() {

                @Override
                public TransactionStatus getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
                    return new SimpleTransactionStatus(true);
                }

                @Override
                public void commit(TransactionStatus transactionStatus) throws TransactionException {
                    txMonitorComponent.registerCommit();
                }

                @Override
                public void rollback(TransactionStatus transactionStatus) throws TransactionException {
                    txMonitorComponent.registerRollback();
                }
            };
        }
    }
}
