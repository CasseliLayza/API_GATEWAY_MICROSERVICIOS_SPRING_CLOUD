package com.backend.springcloud.app.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@SpringBootApplication
//@EnableDiscoveryClient
public class SpringCloudApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudApiGatewayApplication.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> routerConfig() {
        return route("msvc-products")
                .route(path("/api/a/**"), http())
                .filter((request, next) -> {
                    System.out.println("Request: " + request);
                    ServerRequest requestCasse = ServerRequest.from(request)
                            .header("message-request", "Request msj from API A").build();
                    ServerResponse response = next.handle(requestCasse);
                    response.headers().add("message-response", "Response mjs from API A");
                    System.out.println("Response: " + response);
                    return response;
                })
                .before(stripPrefix(2))
                .filter(lb("msvc-products"))
                .filter(circuitBreaker(config -> config
                        .setId("products")
                        .setStatusCodes("500")
                        .setFallbackPath("/api/b/items/find/5")))
                .build();

    }


}
