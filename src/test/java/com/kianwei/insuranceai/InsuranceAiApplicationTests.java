package com.kianwei.insuranceai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test that verifies the Spring application context loads successfully.
 * DataSource auto-configuration is excluded so no real database is required.
 */
@SpringBootTest
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
class InsuranceAiApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the application context starts without errors.
    }
}
