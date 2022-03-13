//package com.servicepoller.servicepoller;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.server.RequestPredicate;
//import org.springframework.web.reactive.function.server.RouterFunction;
//import org.springframework.web.reactive.function.server.ServerResponse;
//
//import static org.springframework.web.reactive.function.server.RequestPredicates.*;
//import static org.springframework.web.reactive.function.server.RouterFunctions.route;
//
//@Configuration
//public class ServiceEndpointConfiguration {
//
//    @Bean
//    RouterFunction<ServerResponse> routes(ServiceHandler handler) {
//        return route(i(GET("/services")), handler::all)
//                .andRoute(i(GET("/services/{id}")), handler::getById)
//                .andRoute(i(DELETE("/services/{id}")), handler::deleteById)
//                .andRoute(i(POST("/services")), handler::create)
//                .andRoute(i(PUT("/services/{id}")), handler::updateById);
//    }
//
//
//    private static RequestPredicate i(RequestPredicate target) {
//        return new CaseInsensitiveRequestPredicate(target);
//    }
//}
