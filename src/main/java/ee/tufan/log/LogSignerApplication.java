package ee.tufan.log;

import ee.tufan.log.storage.service.StorageService;
import ee.tufan.log.storage.service.StorageServiceImplProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageServiceImplProperties.class)
public class LogSignerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogSignerApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.init();
		};
	}

}
