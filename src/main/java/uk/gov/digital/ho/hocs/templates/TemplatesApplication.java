package uk.gov.digital.ho.hocs.templates;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PreDestroy;

@Slf4j
@SpringBootApplication
public class TemplatesApplication {

	public static void main(String[] args) {
		SpringApplication.run(TemplatesApplication.class, args);

	}

	@PreDestroy
	public void stop() {
		log.info("hocs-templates stopping gracefully");
	}

}
