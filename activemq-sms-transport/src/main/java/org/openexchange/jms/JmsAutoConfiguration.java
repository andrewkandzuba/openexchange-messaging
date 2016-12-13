package org.openexchange.jms;

import org.openexchange.pojos.Sms;
import org.openexchange.sms.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.ConnectionFactory;
import javax.jms.Session;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@Configuration
@RefreshScope
@EnableJms
@EnableTransactionManagement
public class JmsAutoConfiguration {
    @Value("${spring.jms.broker.receive.timeout.timeUnit:SECONDS}")
    private String receiveTimeoutTimeUnit;
    @Value("${spring.jms.broker.receive.timeout.interval:1}")
    private long receiveTimeoutInterval;

    @Bean
    @ConditionalOnMissingBean(PlatformTransactionManager.class)
    public PlatformTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new JmsTransactionManager(connectionFactory);
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
        jmsTemplate.setSessionTransacted(true);
        jmsTemplate.setReceiveTimeout(TimeUnit.valueOf(receiveTimeoutTimeUnit).toMillis(receiveTimeoutInterval));
        return jmsTemplate;
    }

    @ConditionalOnMissingBean(SmsService.class)
    @Bean
    public SmsService smsService(JmsTemplate jmsTemplate) {
        return new SmsService() {
            private final Logger logger = LoggerFactory.getLogger(SmsService.class);

            @Override
            public Collection<Sms> receive(int number) {
                Collection<Sms> messages = new HashSet<>();
                while (number-- > 0) {
                    Sms message = (Sms) jmsTemplate.receiveAndConvert(queueName);
                    if (message == null) {
                        logger.debug("No message is available");
                        break;
                    }
                    messages.add(message);
                    logger.debug("Message has been received: [" + message + "]");
                }
                return messages;
            }

            @Override
            @Transactional(noRollbackFor = Throwable.class)
            public void send(Collection<Sms> messages) throws JmsException {
                messages.forEach(message -> {
                    logger.debug("Message has been sent: [" + message + "]");
                    jmsTemplate.convertAndSend(queueName, message);
                });
            }
        };
    }
}
