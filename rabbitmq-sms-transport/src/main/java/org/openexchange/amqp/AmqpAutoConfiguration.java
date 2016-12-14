package org.openexchange.amqp;

import org.openexchange.pojos.Sms;
import org.openexchange.sms.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;

import static org.openexchange.sms.SmsService.queueName;

@Configuration
@RefreshScope
@EnableTransactionManagement
public class AmqpAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(PlatformTransactionManager.class)
    public PlatformTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new RabbitTransactionManager(connectionFactory);
    }

    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMultiplier(10.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        template.setRetryTemplate(retryTemplate);
        return template;
    }

    @Bean
    public Queue myDurableQueue() {
        return new Queue(queueName, true, false, false);
    }

    @ConditionalOnMissingBean(SmsService.class)
    @Bean
    public SmsService smsService(AmqpTemplate rabbitTemplate) {
        return new SmsService() {
            private final Logger logger = LoggerFactory.getLogger(SmsService.class);

            @Override
            public Collection<Sms> receive(int number) {
                Collection<Sms> messages = new HashSet<>();
                while (number-- > 0) {
                    Sms message = (Sms) rabbitTemplate.receiveAndConvert(queueName);
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
            public void send(Collection<Sms> messages) {
                messages.forEach(message -> {
                    rabbitTemplate.convertAndSend(queueName, message);
                    logger.debug(String.format("Message: [%s] has been sent to [%s] queue", message, queueName));
                });
            }
        };
    }
}
