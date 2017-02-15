package io.openexchange.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTable;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmsReceiptIT.class)
@ComponentScan(basePackages = "io.openexchange.integration")
@TestPropertySource(locations = "classpath:test.properties")
public class SmsReceiptIT {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void smsReceipt() throws Exception {
        int retries = 100;
        int registered = 0;
        while (--retries > 0 && registered == 0) {
            registered += countRowsInTable(jdbcTemplate, "sms");
            Thread.sleep(1000);
        }
        assertTrue(registered > 0);
    }
}
