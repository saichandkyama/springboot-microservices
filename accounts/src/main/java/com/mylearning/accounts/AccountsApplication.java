package com.mylearning.accounts;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@OpenAPIDefinition(info = @Info(
        title = "Accounts Microservice Rest API",
        version = "v1",
        description = "Accounts Microservice Rest API Documentation",
        contact = @Contact(
                name = "Saichand Kyama",
                email = "saichand@gmail.com",
                url = "https://www.sspbank.com"
        ),
        license = @License(
                name = "Apache 2.0",
                url = "https://www.sspbank.com"
        )
),
        externalDocs = @ExternalDocumentation(description = "Accounts Microservice Rest API Documentation",
                url = "https://www.sspbank.com/swagger-ui.html"))
public class AccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountsApplication.class, args);
    }

}
