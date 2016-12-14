package org.openexchange.sms;

import org.openexchange.pojos.Sms;

import java.util.Collection;

public interface SmsService {
    String queueName = "sms.outbound.queue";

    void send(Collection<Sms> messages);
    Collection<Sms> receive(int number);
}
