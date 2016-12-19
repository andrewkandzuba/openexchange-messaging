package io.openexchange.aop;

import io.openexchange.pojos.Sms;
import io.openexchange.sms.SmsService;

import java.util.Collection;

class SmsServiceMock implements SmsService {
    private Collection<Sms> inbox;

    @Override
    public void send(Collection<Sms> messages) {

    }

    @Override
    public Collection<Sms> receive(int number) {
        return inbox;
    }

    void setInbox(Collection<Sms> inbox){
        this.inbox = inbox;
    }
}
