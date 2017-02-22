package io.openexchange.producers;

import io.openexchange.pojos.Sms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.*;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SmsProducer {
    private final static Logger logger = LoggerFactory.getLogger(SmsProducer.class);

    @Autowired
    private Source source;

    @Transactional
    public boolean send(Sms sms) {
        logger.debug("Validating sms:[" + sms + "]");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Sms>> violations = validator.validate(sms);

        if(violations.size() > 0){
            String violationErrors  = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "));
            throw new ValidationException("Validation errors have been detected: +[" + violationErrors + "]");
        }

        logger.debug("Sending sms:[" + sms + "]");
        return source.output().send(MessageBuilder.withPayload(sms).build());
    }
}