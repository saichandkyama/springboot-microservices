package com.mylearning.gatewayserver.filters;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@Component
public class FilterUtility {

    public static final String CORRELATION_ID = "sspbank-correlation-id";

    /**
         * Retrieves the correlation ID from the given HTTP request headers.
         *
         * @param requestHeaders the HTTP headers from which to extract the correlation ID
         * @return the correlation ID if present, otherwise null
         */
        public String getCorrelationId(HttpHeaders requestHeaders) {
            if (requestHeaders.get(CORRELATION_ID) != null) {
                List<String> requestHeaderList = requestHeaders.get(CORRELATION_ID);
                return requestHeaderList.stream().findFirst().get();
            } else {
                return null;
            }
        }

    /**
         * Sets a header on the HTTP request within the given ServerWebExchange.
         *
         * @param exchange the current ServerWebExchange
         * @param name the name of the header to set
         * @param value the value of the header to set
         * @return a mutated ServerWebExchange with the updated header
         */
        public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
            return exchange.mutate().request(exchange.getRequest().mutate().header(name, value).build()).build();
        }

    /**
         * Sets the correlation ID header on the HTTP request within the given ServerWebExchange.
         *
         * @param exchange the current ServerWebExchange
         * @param correlationId the correlation ID value to set
         * @return a mutated ServerWebExchange with the correlation ID header set
         */
        public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
            return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
        }
}
