package com.example.demoapp.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for CustomMetricsConfiguration
 * Tests the metrics configuration in a Spring context with minimal dependencies
 */
@SpringJUnitConfig
public class CustomMetricsConfigurationIntegrationTest {

    private MeterRegistry meterRegistry;
    private CustomMetricsConfiguration customMetricsConfiguration;
    private Counter userCreationCounter;
    private Counter userRetrievalCounter;
    private Timer userCreationTimer;
    private Timer mongoQueryTimer;

    @TestConfiguration
    @Import(CustomMetricsConfiguration.class)
    static class TestConfig {
        @Bean
        public MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    @BeforeEach
    public void setUp() {
        // Create a fresh registry for each test
        meterRegistry = new SimpleMeterRegistry();
        customMetricsConfiguration = new CustomMetricsConfiguration(meterRegistry);
        
        // Initialize the metrics
        customMetricsConfiguration.bindTo(meterRegistry);
        
        // Get the metrics
        userCreationCounter = customMetricsConfiguration.userCreationCounter();
        userRetrievalCounter = customMetricsConfiguration.userRetrievalCounter();
        userCreationTimer = customMetricsConfiguration.userCreationTimer();
        mongoQueryTimer = customMetricsConfiguration.mongoQueryTimer();
    }

    @Test
    public void testMetricsBeansAreCreated() {
        // Verify all beans are created correctly
        assertNotNull(meterRegistry);
        assertNotNull(customMetricsConfiguration);
        assertNotNull(userCreationCounter);
        assertNotNull(userRetrievalCounter);
        assertNotNull(userCreationTimer);
        assertNotNull(mongoQueryTimer);
    }

    @Test
    public void testCounterMetricsWork() {
        // Get initial counts
        double initialUserCreationCount = userCreationCounter.count();
        double initialUserRetrievalCount = userRetrievalCounter.count();

        // Increment counters
        userCreationCounter.increment();
        userCreationCounter.increment();
        userRetrievalCounter.increment();

        // Verify counters were incremented
        assertEquals(initialUserCreationCount + 2, userCreationCounter.count());
        assertEquals(initialUserRetrievalCount + 1, userRetrievalCounter.count());

        // Verify metrics are registered in the registry
        assertNotNull(meterRegistry.find("users.created").counter());
        assertNotNull(meterRegistry.find("users.retrieved").counter());
    }

    @Test
    public void testTimerMetricsWork() throws Exception {
        // Get initial counts
        long initialUserCreationTimerCount = userCreationTimer.count();
        long initialMongoQueryTimerCount = mongoQueryTimer.count();

        // Record timings
        userCreationTimer.record(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // Ignore
            }
        });

        mongoQueryTimer.record(20, TimeUnit.MILLISECONDS);

        // Verify timers recorded events
        assertEquals(initialUserCreationTimerCount + 1, userCreationTimer.count());
        assertEquals(initialMongoQueryTimerCount + 1, mongoQueryTimer.count());

        // Verify metrics are registered in the registry
        assertNotNull(meterRegistry.find("users.creation.time").timer());
        assertNotNull(meterRegistry.find("mongodb.query.time").timer());
    }

    @Test
    public void testBindToMethod() {
        // Verify that the metrics were registered correctly
        assertNotNull(meterRegistry.find("users.created").counter());
        assertNotNull(meterRegistry.find("users.retrieved").counter());
        assertNotNull(meterRegistry.find("users.creation.time").timer());
        assertNotNull(meterRegistry.find("mongodb.query.time").timer());
    }
}
