package com.example.demoapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@TestPropertySource(properties = {
    "spring.main.web-application-type=none" // Disable web server startup
})
class DemoAppApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		assertNotNull(applicationContext, "Application context should not be null");
	}
	
	@Test
	void applicationMainMethod(CapturedOutput output) {
		// Create a separate args array with a property to prevent server startup
		String[] args = {"--spring.main.web-application-type=none"};
		
		// Test the main method
		DemoAppApplication.main(args);
		
		// Verify application started by checking logs
		assertTrue(output.toString().contains("Started DemoAppApplication"), 
				"Application should have started successfully");
	}
}
