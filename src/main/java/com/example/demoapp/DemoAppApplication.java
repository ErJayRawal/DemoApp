package com.example.demoapp;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class DemoAppApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(DemoAppApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoAppApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner initTestMetric(MeterRegistry meterRegistry) {
		return args -> {
			logger.info("Creating test metric directly in main application class");
			Counter testCounter = Counter.builder("test.metric.counter")
					.description("A test metric counter")
					.tags("application", "demoapp")
					.register(meterRegistry);
			
			testCounter.increment();
			logger.info("Test metric created and incremented: {}", testCounter.getId().getName());
		};
	}
}
