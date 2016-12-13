package org.openexchange.jpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:test.properties")
public class SmsEntityRepositoryTest {
    @Inject
    private SmsRepository smsRepository;

    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplicates() throws Exception {
        String messageId = UUID.randomUUID().toString();
        Collection<SmsEntity> messages = new HashSet<>();
        messages.add(new SmsEntity(messageId, "+37258211717", "+37258000000", "Sample text1", new Date()));
        messages.add(new SmsEntity(messageId, "+37258211718", "+37258000001", "Sample text2", new Date()));
        smsRepository.save(messages);
    }
}
