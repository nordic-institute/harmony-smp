package eu.europa.ec.edelivery.smp.monitor;

import eu.europa.ec.edelivery.smp.ui.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MonitorController integration tests
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class MonitorControllerIT extends AbstractControllerTest {

    private static final String URL = "/monitor/is-alive";
    private static final RequestPostProcessor ADMIN_CREDENTIALS = httpBasic("pat_smp_admin", "123456");

    @Autowired
    private MonitorController testInstance;

    @BeforeEach
    public void setup() throws IOException {
        super.setup();
    }

    @Test
    public void isAliveNotAuthorized() throws Exception {
        mvc.perform(get(URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void isAlive() throws Exception {
        mvc.perform(get(URL)
                        .with(ADMIN_CREDENTIALS))
                .andExpect(status()
                        .isOk());
    }

    @Test
    public void testDatabase() {
        // when
        boolean result = testInstance.testDatabase();

        assertTrue(result);

    }
}
