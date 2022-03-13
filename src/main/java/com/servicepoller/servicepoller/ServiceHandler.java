package com.servicepoller.servicepoller;

import com.servicepoller.servicepoller.dto.NewServiceDTO;
import com.servicepoller.servicepoller.entity.Service;
import com.servicepoller.servicepoller.service.ServicesService;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class ServiceHandler {

    private final ServicesService service;

    public ServiceHandler(ServicesService service) {
        this.service = service;
    }


    Mono<ServerResponse> getById(ServerRequest r) {
        return defaultReadResponse(this.service.get(id(r)));
    }

    Mono<ServerResponse> all(ServerRequest r) {
        return defaultReadResponse(this.service.all());
    }

    Mono<ServerResponse> deleteById(ServerRequest r) {
        return defaultReadResponse(this.service.delete(id(r)));
    }

    Mono<ServerResponse> updateById(ServerRequest r) {
        Flux<Service> id = r.bodyToFlux(Service.class)
                .flatMap(p -> this.service.update(id(r), p.getName(), p.getUrl()));
        return defaultReadResponse(id);
    }

    Mono<ServerResponse> create(ServerRequest request) {
        Flux<Service> flux = request
                .bodyToFlux(NewServiceDTO.class)
                .flatMap(toWrite -> this.service.create(toWrite.getName(), toWrite.getUrl()));
        return defaultWriteResponse(flux);
    }


    private static Mono<ServerResponse> defaultWriteResponse(Publisher<Service> profiles) {
        return Mono
                .from(profiles)
                .flatMap(p -> ServerResponse
                        .created(URI.create("/services/" + p.getId()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build()
                );
    }


    private static Mono<ServerResponse> defaultReadResponse(Publisher<Service> profiles) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(profiles, Service.class);
    }

    private static String id(ServerRequest r) {
        return r.pathVariable("id");
    }
}
