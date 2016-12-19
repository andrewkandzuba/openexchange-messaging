package io.openexchange.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import io.openexchange.pojos.Sms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

@Aspect
@Component
public class SmsServiceMonitoring {
    private static final Logger logger = LoggerFactory.getLogger(SmsServiceMonitoring.class);
    private static void validate(Collection<Sms> messages){
        messages.forEach(sms -> {
            Objects.requireNonNull(sms.getMessageId());
            Objects.requireNonNull(sms.getMobileOriginate());
            Objects.requireNonNull(sms.getMobileTerminate());
            Objects.requireNonNull(sms.getReceiveTime());
            Objects.requireNonNull(sms.getText());
        });
    }

    @Before("execution(* io.openexchange.sms.SmsService.send(*)) && args(messages)")
    public void beforeSend(Collection<Sms> messages) {
        logger.debug("SmsServiceAspect: before sms service send");
        validate(messages);
    }

    @After("execution(* io.openexchange.sms.SmsService.send(*)) && args(messages)")
    public void afterSend(Collection<Sms> messages) {
        logger.debug("SmsServiceAspect: after sms service send");
    }

    @AfterReturning(pointcut = "execution(* io.openexchange.sms.SmsService.receive(*))",
            returning="messages")
    public void afterReceive(Collection<Sms> messages) {
        logger.debug("SmsServiceAspect: after sms service received");
        validate(messages);
    }
}
