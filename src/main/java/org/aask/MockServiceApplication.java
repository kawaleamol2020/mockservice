package org.aask;

import org.aask.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJms
@SpringBootApplication
@EnableAsync
@EnableCaching
@ServletComponentScan
@EnableAutoConfiguration(exclude = { ErrorMvcAutoConfiguration.class })
public class MockServiceApplication extends SpringBootServletInitializer implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(MockServiceApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(MockServiceApplication.class);
	}

	@Autowired
	private ConfigurationService configurationService;
	
	@Override
	public void run(String... args) throws Exception {

		try {
			configurationService.clearCache();
			configurationService.getMockTypes();
			configurationService.getConfigurationPropertyTypes();
			logger.info("configuration loaded successfully");
		} catch (Exception e) {	}

	}
}
