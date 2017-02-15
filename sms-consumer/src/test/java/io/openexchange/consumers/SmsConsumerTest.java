package io.openexchange.consumers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmsConsumerTest.class)
@TestPropertySource(locations = "classpath:test.properties")
public class SmsConsumerTest {
    @Test
    public void testRandomFailure() throws Exception {

    }
}
