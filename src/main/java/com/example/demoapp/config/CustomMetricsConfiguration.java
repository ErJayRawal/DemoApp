package com.example.demoapp.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import jakarta.annotation.PostConstruct;

/**
 * Configuration for custom application metrics
 */
@Configuration
public class CustomMetricsConfiguration implements MeterBinder {

    private final MeterRegistry meterRegistry;
    private static final Logger logger = LoggerFactory.getLogger(CustomMetricsConfiguration.class);
    
    private Counter userCreationCounterInstance;
    private Counter userRetrievalCounterInstance;
    private Timer userCreationTimerInstance;
    private Timer mongoQueryTimerInstance;
    
    @Autowired
    public CustomMetricsConfiguration(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        logger.info("CustomMetricsConfiguration initialized with MeterRegistry: {}", meterRegistry);
    }
    
    @Override
    public void bindTo(MeterRegistry registry) {
        logger.info("Binding metrics to registry: {}", registry);
        
        // Register metrics directly using the MeterBinder approach with standard naming convention
        userCreationCounterInstance = Counter.builder("users.created")
               .description("Number of users created")
               .tags("application", "demoapp")
               .register(registry);
        logger.info("Registered userCreationCounter: {}", userCreationCounterInstance.getId().getName());
        
        userRetrievalCounterInstance = Counter.builder("users.retrieved")
               .description("Number of users retrieved")
               .tags("application", "demoapp")
               .register(registry);
        logger.info("Registered userRetrievalCounter: {}", userRetrievalCounterInstance.getId().getName());
        
        userCreationTimerInstance = Timer.builder("users.creation.time")
              .description("Time taken to create users")
              .tags("application", "demoapp")
              .register(registry);
        logger.info("Registered userCreationTimer: {}", userCreationTimerInstance.getId().getName());
        
        mongoQueryTimerInstance = Timer.builder("mongodb.query.time")
              .description("Time taken for MongoDB queries")
              .tags("application", "demoapp")
              .register(registry);
        logger.info("Registered mongoQueryTimer: {}", mongoQueryTimerInstance.getId().getName());
              
        logger.info("Custom metrics registered successfully via MeterBinder");
    }

    /**
     * Counter for tracking user creation events
     */
    @Bean(name = "userCreationCounter")
    @Primary
    public Counter userCreationCounter() {
        if (userCreationCounterInstance != null) {
            logger.info("Returning existing userCreationCounter: {}", userCreationCounterInstance.getId().getName());
            return userCreationCounterInstance;
        }
        
        Counter counter = meterRegistry.counter("users.created", "application", "demoapp");
        logger.info("Created new userCreationCounter: {}", counter.getId().getName());
        return counter;
    }

    /**
     * Counter for tracking user retrieval events
     */
    @Bean(name = "userRetrievalCounter")
    @Primary
    public Counter userRetrievalCounter() {
        if (userRetrievalCounterInstance != null) {
            logger.info("Returning existing userRetrievalCounter: {}", userRetrievalCounterInstance.getId().getName());
            return userRetrievalCounterInstance;
        }
        
        Counter counter = meterRegistry.counter("users.retrieved", "application", "demoapp");
        logger.info("Created new userRetrievalCounter: {}", counter.getId().getName());
        return counter;
    }

    /**
     * Timer for measuring user creation latency
     */
    @Bean(name = "userCreationTimer")
    @Primary
    public Timer userCreationTimer() {
        if (userCreationTimerInstance != null) {
            logger.info("Returning existing userCreationTimer: {}", userCreationTimerInstance.getId().getName());
            return userCreationTimerInstance;
        }
        
        Timer timer = meterRegistry.timer("users.creation.time", "application", "demoapp");
        logger.info("Created new userCreationTimer: {}", timer.getId().getName());
        return timer;
    }

    /**
     * Timer for measuring MongoDB query latency
     */
    @Bean(name = "mongoQueryTimer")
    @Primary
    public Timer mongoQueryTimer() {
        if (mongoQueryTimerInstance != null) {
            logger.info("Returning existing mongoQueryTimer: {}", mongoQueryTimerInstance.getId().getName());
            return mongoQueryTimerInstance;
        }
        
        Timer timer = meterRegistry.timer("mongodb.query.time", "application", "demoapp");
        logger.info("Created new mongoQueryTimer: {}", timer.getId().getName());
        return timer;
    }
}
