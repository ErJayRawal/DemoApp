package com.example.demoapp.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test class for CustomMetricsConfiguration
 * Aims to achieve 100% line and branch coverage
 */
@ExtendWith(MockitoExtension.class)
public class CustomMetricsConfigurationTest {

    private CustomMetricsConfiguration configuration;
    private SimpleMeterRegistry simpleMeterRegistry;

    @BeforeEach
    public void setUp() {
        // Create a real SimpleMeterRegistry for some tests
        simpleMeterRegistry = new SimpleMeterRegistry();
    }

    @Test
    public void testConstructor() {
        // Test the constructor
        configuration = new CustomMetricsConfiguration(simpleMeterRegistry);
        assertNotNull(configuration);
    }

    @Test
    public void testBindTo() {
        // Test the bindTo method with a real registry
        configuration = new CustomMetricsConfiguration(simpleMeterRegistry);
        configuration.bindTo(simpleMeterRegistry);
        
        // Verify that metrics were registered
        assertNotNull(simpleMeterRegistry.find("users.created").counter());
        assertNotNull(simpleMeterRegistry.find("users.retrieved").counter());
        assertNotNull(simpleMeterRegistry.find("users.creation.time").timer());
        assertNotNull(simpleMeterRegistry.find("mongodb.query.time").timer());
    }

    @Test
    public void testUserCreationCounter_NewInstance() {
        MeterRegistry mockRegistry = mock(MeterRegistry.class);
        Counter mockCounter = mock(Counter.class);
        Meter.Id mockId = mock(Meter.Id.class);
        when(mockCounter.getId()).thenReturn(mockId);
        when(mockId.getName()).thenReturn("users.created");
        when(mockRegistry.counter(eq("users.created"), eq("application"), eq("demoapp"))).thenReturn(mockCounter);
        
        // Test creating a new counter when instance is null
        configuration = new CustomMetricsConfiguration(mockRegistry);
        Counter counter = configuration.userCreationCounter();
        
        // Verify that a new counter was created
        verify(mockRegistry).counter(eq("users.created"), eq("application"), eq("demoapp"));
        assertNotNull(counter);
        assertEquals("users.created", counter.getId().getName());
    }

    @Test
    public void testUserCreationCounter_ExistingInstance() {
        // Test returning existing counter instance
        configuration = new CustomMetricsConfiguration(simpleMeterRegistry);
        configuration.bindTo(simpleMeterRegistry); // This will create the instance
        
        // Now call the bean method - should return existing instance
        Counter counter = configuration.userCreationCounter();
        
        // Verify we got a counter and it's the same one
        assertNotNull(counter);
        assertEquals("users.created", counter.getId().getName());
    }

    @Test
    public void testUserRetrievalCounter_NewInstance() {
        MeterRegistry mockRegistry = mock(MeterRegistry.class);
        Counter mockCounter = mock(Counter.class);
        Meter.Id mockId = mock(Meter.Id.class);
        when(mockCounter.getId()).thenReturn(mockId);
        when(mockId.getName()).thenReturn("users.retrieved");
        when(mockRegistry.counter(eq("users.retrieved"), eq("application"), eq("demoapp"))).thenReturn(mockCounter);
        
        // Test creating a new counter when instance is null
        configuration = new CustomMetricsConfiguration(mockRegistry);
        Counter counter = configuration.userRetrievalCounter();
        
        // Verify that a new counter was created
        verify(mockRegistry).counter(eq("users.retrieved"), eq("application"), eq("demoapp"));
        assertNotNull(counter);
        assertEquals("users.retrieved", counter.getId().getName());
    }

    @Test
    public void testUserRetrievalCounter_ExistingInstance() {
        // Test returning existing counter instance
        configuration = new CustomMetricsConfiguration(simpleMeterRegistry);
        configuration.bindTo(simpleMeterRegistry); // This will create the instance
        
        // Now call the bean method - should return existing instance
        Counter counter = configuration.userRetrievalCounter();
        
        // Verify we got a counter and it's the same one
        assertNotNull(counter);
        assertEquals("users.retrieved", counter.getId().getName());
    }

    @Test
    public void testUserCreationTimer_NewInstance() {
        MeterRegistry mockRegistry = mock(MeterRegistry.class);
        Timer mockTimer = mock(Timer.class);
        Meter.Id mockId = mock(Meter.Id.class);
        when(mockTimer.getId()).thenReturn(mockId);
        when(mockId.getName()).thenReturn("users.creation.time");
        when(mockRegistry.timer(eq("users.creation.time"), eq("application"), eq("demoapp"))).thenReturn(mockTimer);
        
        // Test creating a new timer when instance is null
        configuration = new CustomMetricsConfiguration(mockRegistry);
        Timer timer = configuration.userCreationTimer();
        
        // Verify that a new timer was created
        verify(mockRegistry).timer(eq("users.creation.time"), eq("application"), eq("demoapp"));
        assertNotNull(timer);
        assertEquals("users.creation.time", timer.getId().getName());
    }

    @Test
    public void testUserCreationTimer_ExistingInstance() {
        // Test returning existing timer instance
        configuration = new CustomMetricsConfiguration(simpleMeterRegistry);
        configuration.bindTo(simpleMeterRegistry); // This will create the instance
        
        // Now call the bean method - should return existing instance
        Timer timer = configuration.userCreationTimer();
        
        // Verify we got a timer and it's the same one
        assertNotNull(timer);
        assertEquals("users.creation.time", timer.getId().getName());
    }

    @Test
    public void testMongoQueryTimer_NewInstance() {
        MeterRegistry mockRegistry = mock(MeterRegistry.class);
        Timer mockTimer = mock(Timer.class);
        Meter.Id mockId = mock(Meter.Id.class);
        when(mockTimer.getId()).thenReturn(mockId);
        when(mockId.getName()).thenReturn("mongodb.query.time");
        when(mockRegistry.timer(eq("mongodb.query.time"), eq("application"), eq("demoapp"))).thenReturn(mockTimer);
        
        // Test creating a new timer when instance is null
        configuration = new CustomMetricsConfiguration(mockRegistry);
        Timer timer = configuration.mongoQueryTimer();
        
        // Verify that a new timer was created
        verify(mockRegistry).timer(eq("mongodb.query.time"), eq("application"), eq("demoapp"));
        assertNotNull(timer);
        assertEquals("mongodb.query.time", timer.getId().getName());
    }

    @Test
    public void testMongoQueryTimer_ExistingInstance() {
        // Test returning existing timer instance
        configuration = new CustomMetricsConfiguration(simpleMeterRegistry);
        configuration.bindTo(simpleMeterRegistry); // This will create the instance
        
        // Now call the bean method - should return existing instance
        Timer timer = configuration.mongoQueryTimer();
        
        // Verify we got a timer and it's the same one
        assertNotNull(timer);
        assertEquals("mongodb.query.time", timer.getId().getName());
    }
    
    @Test
    public void testMetricFunctionality() {
        // Integration test to verify metrics actually work
        configuration = new CustomMetricsConfiguration(simpleMeterRegistry);
        configuration.bindTo(simpleMeterRegistry);
        
        // Get the counters and timers
        Counter userCreationCounter = configuration.userCreationCounter();
        Counter userRetrievalCounter = configuration.userRetrievalCounter();
        Timer userCreationTimer = configuration.userCreationTimer();
        Timer mongoQueryTimer = configuration.mongoQueryTimer();
        
        // Increment counters
        userCreationCounter.increment();
        userRetrievalCounter.increment(2);
        
        // Record timings
        userCreationTimer.record(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // Ignore
            }
        });
        
        mongoQueryTimer.record(() -> {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                // Ignore
            }
        });
        
        // Verify metrics were recorded
        assertEquals(1, userCreationCounter.count());
        assertEquals(2, userRetrievalCounter.count());
        assertEquals(1, userCreationTimer.count());
        assertEquals(1, mongoQueryTimer.count());
    }
}
