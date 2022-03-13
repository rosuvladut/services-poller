package com.servicepoller.servicepoller.controller;

import com.servicepoller.servicepoller.dto.NewServiceDTO;
import com.servicepoller.servicepoller.entity.Service;
import com.servicepoller.servicepoller.service.ServicesService;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/services", produces = MediaType.APPLICATION_JSON_VALUE)
//@Profile("classic")
public class ServicePollerController {

    private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
    private final ServicesService service;

    public ServicePollerController(ServicesService service) {
        this.service = service;
    }


    @GetMapping
    Publisher<Service> getAll() {
        return this.service.all();
    }


    @GetMapping("/{id}")
    Publisher<Service> getById(@PathVariable("id") String id) {
        return this.service.get(id);
    }


    @PostMapping
    Mono<Service> create(@RequestBody NewServiceDTO newService) {
        return this.service
                .create(newService.getName(), newService.getUrl());
    }

    @DeleteMapping("/{id}")
    Publisher<Service> deleteById(@PathVariable String id) {
        return this.service.delete(id);
    }

    @PutMapping("/{id}")
    Publisher<ResponseEntity<Service>> updateById(@PathVariable String id, @RequestBody Service service) {
        return Mono
                .just(service)
                .flatMap(p -> this.service.update(id, p.getName(), p.getUrl()))
                .map(p -> ResponseEntity
                        .ok()
                        .contentType(this.mediaType)
                        .build());
    }
}
