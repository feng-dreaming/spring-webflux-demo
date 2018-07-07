/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.saker.webflux.demo.router;

import lombok.extern.slf4j.Slf4j;
import me.saker.webflux.demo.handlers.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 *
 * @author jing
 */
@Slf4j
@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(UserHandler userHandler) {
        return RouterFunctions.nest(RequestPredicates.path("/api/users"),
                RouterFunctions
                        .route(GET(""), userHandler::findAllUsers)
                        .andRoute(GET("/{id}"), userHandler::findUser)
                        .andRoute(POST("").and(contentType(APPLICATION_JSON)), userHandler::createUser)
                        .andRoute(PUT("/{id}").and(contentType(APPLICATION_JSON)), userHandler::editUser)
                        .andRoute(DELETE("/{id}"), userHandler::deleteUser)
        ).filter((request, next) -> {
            long begin = System.currentTimeMillis();
            log.info("before: {}", request.uri());
            Mono<ServerResponse> response = next.handle(request);
            log.info("after: {} ms", System.currentTimeMillis() - begin);
            return response;
        });
    }
}
