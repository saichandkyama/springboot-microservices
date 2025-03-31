package com.mylearning.cards;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
/*@ComponentScans({ @ComponentScan("com.mylearning.cards.controller") })
@EnableJpaRepositories("com.mylearning.cards.repository")
@EntityScan("com.mylearning.cards.entity")*/
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@OpenAPIDefinition(
		info = @Info(
				title = "Cards microservice REST API Documentation",
				description = "SSP Bank Cards microservice REST API Documentation",
				version = "v1",
				contact = @Contact(
						name = "Saichand K",
						email = "saichand@gmail.com",
						url = "https://www.saichandportfolio.com"
				),
				license = @License(
						name = "Apache 2.0",
						url = "https://www.sspbank.com"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "SSP Bank Cards microservice REST API Documentation",
				url = "https://www.sspbank.com/swagger-ui.html"
		)
)
public class CardsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardsApplication.class, args);
	}

}
