package com.mylearning.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(1)
@Component
public class RequestTraceFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);

    @Autowired
    FilterUtility filterUtility;

    /**
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        if (isCorrelationIdPresent(requestHeaders)) {
            logger.debug("sspBank-correlation-id found in RequestTraceFilter : {}",
                    filterUtility.getCorrelationId(requestHeaders));
        } else {
            String correlationID = generateCorrelationId();
            exchange = filterUtility.setCorrelationId(exchange, correlationID);
            logger.debug("sspBank-correlation-id generated in RequestTraceFilter : {}", correlationID);
        }
        return chain.filter(exchange);
    }

    /**
         * Checks if the correlation ID is present in the request headers.
         *
         * @param requestHeaders the HTTP request headers
         * @return true if the correlation ID is present, false otherwise
         */
        private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
            if (filterUtility.getCorrelationId(requestHeaders) != null) {
                return true;
            } else {
                return false;
            }
        }

    /**
         * Generates a new unique correlation ID using UUID.
         *
         * @return a randomly generated correlation ID as a String
         */
        private String generateCorrelationId() {
            return java.util.UUID.randomUUID().toString();
        }
}
