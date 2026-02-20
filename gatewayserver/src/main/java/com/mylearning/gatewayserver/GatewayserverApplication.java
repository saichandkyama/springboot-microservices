package com.mylearning.gatewayserver;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;
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
									.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
									.retry(retryConfig -> retryConfig.setRetries(3)
											.setMethods(HttpMethod.GET)
											.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000),2,true)))
							.uri("lb://LOANS"))
					.route(p -> p
							.path("/sspbank/cards/**")
							.filters( f -> f.rewritePath("/sspbank/cards/(?<segment>.*)","/${segment}")
									.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
							        .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
									.setKeyResolver(userKeyResolver())))
							.uri("lb://CARDS")).build();
		}

	/**
		 * Provides a default customizer for the ReactiveResilience4JCircuitBreakerFactory.
		 * <p>
		 * - Configures the default circuit breaker with Resilience4J's default settings.
		 * - Sets a time limiter with a timeout duration of 4 seconds for all circuit breakers.
		 *
		 * @return a Customizer that applies the default configuration to all circuit breakers
		 */
		@Bean
		public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
			return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
					.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
					.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build()).build());
		}

	@Bean
	public RedisRateLimiter redisRateLimiter() {
		return new RedisRateLimiter(1, 1, 1);
	}

	@Bean
	KeyResolver userKeyResolver() {
		return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
				.defaultIfEmpty("anonymous");
	}

}
