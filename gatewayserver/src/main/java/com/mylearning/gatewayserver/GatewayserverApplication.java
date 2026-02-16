package com.mylearning.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}


	/**
		 * Configures the routes for the Spring Cloud Gateway.
		 * <p>
		 * - Rewrites paths for /sspbank/accounts/\*\*, /sspbank/loans/\*\*, and /sspbank/cards/\*\* to remove the prefix.
		 * - Adds an X-Response-Time header with the current timestamp to each response.
		 * - Forwards requests to the appropriate load-balanced service (ACCOUNTS, LOANS, CARDS).
		 *
		 * @param routeLocatorBuilder the builder for creating route definitions
		 * @return the configured RouteLocator
		 */
	    @Bean
		public RouteLocator sspBankRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
			return routeLocatorBuilder.routes()
					.route(p -> p
							.path("/sspbank/accounts/**")
							.filters( f -> f.rewritePath("/sspbank/accounts/(?<segment>.*)","/${segment}")
									.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
									.circuitBreaker(config -> config.setName("accountsCircuitBreaker")
											.setFallbackUri("forward:/contactSupport")))
							.uri("lb://ACCOUNTS"))
					.route(p -> p
							.path("/sspbank/loans/**")
							.filters( f -> f.rewritePath("/sspbank/loans/(?<segment>.*)","/${segment}")
									.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
							.uri("lb://LOANS"))
					.route(p -> p
							.path("/sspbank/cards/**")
							.filters( f -> f.rewritePath("/sspbank/cards/(?<segment>.*)","/${segment}")
									.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
							.uri("lb://CARDS")).build();
		}

}
