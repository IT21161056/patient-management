package com.pm.apigateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global Exception Handler
 *
 * This class is responsible for handling exceptions that occur
 * anywhere in the API Gateway application.
 *
 * Instead of allowing exceptions to propagate to the client,
 * we can catch them here and return a proper HTTP response.
 */
@RestControllerAdvice // Registers this class as a global exception handler for all controllers.
public class JwtValidationException {

    /**
     * Handles 401 Unauthorized exceptions thrown by WebClient.
     *
     * When the API Gateway communicates with another service
     * (such as an Authentication Service) using WebClient,
     * that service may return a 401 Unauthorized response
     * if the JWT token is invalid, expired, or missing.
     *
     * WebClient converts that HTTP response into the exception:
     *
     * WebClientResponseException.Unauthorized
     *
     * This method catches that exception and returns
     * a 401 response to the original client.
     *
     * Example Flow:
     *
     * Client Request
     *       |
     *       v
     * API Gateway
     *       |
     *       v
     * Auth Service
     *       |
     *       +----> 401 Unauthorized
     *                    |
     *                    v
     *   WebClientResponseException.Unauthorized
     *                    |
     *                    v
     *        This Exception Handler
     *                    |
     *                    v
     *       Returns 401 to Client
     */

    @ExceptionHandler(WebClientResponseException.Unauthorized.class)
    // Spring automatically invokes this method whenever
    // a WebClientResponseException.Unauthorized is thrown.
    public Mono<Void> handleUnauthorizedException(ServerWebExchange exchange) {

        /**
         * ServerWebExchange
         *
         * Represents the current HTTP request and response.
         *
         * Through this object we can:
         * - Read request details
         * - Modify response headers
         * - Set status codes
         * - Complete the response
         */

        // Set HTTP status code to 401 Unauthorized.
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        /**
         * setComplete()
         *
         * Completes the HTTP response and sends it back to the client.
         *
         * Return Type:
         *      Mono<Void>
         *
         * Why Mono<Void>?
         *
         * Spring WebFlux is reactive and non-blocking.
         * Instead of immediately returning a response,
         * it returns a Mono that represents a future completion signal.
         *
         * Mono = A reactive publisher that emits:
         *      - Zero values, or
         *      - One value
         *
         * Mono<Void> specifically means:
         *      "No data will be returned,
         *       only a completion notification."
         *
         * In this case:
         *      - No response body is sent.
         *      - Only the 401 status code is returned.
         *      - Mono signals when the response has finished.
         */
        return exchange.getResponse().setComplete();
    }
}