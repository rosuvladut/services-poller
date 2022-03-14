package com.servicepoller.servicepoller.controller;

import com.servicepoller.servicepoller.dto.NewServiceDTO;
import com.servicepoller.servicepoller.entity.Service;
import com.servicepoller.servicepoller.service.ServicesService;
import org.apache.commons.validator.routines.UrlValidator;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/services", produces = MediaType.APPLICATION_JSON_VALUE)
public class ServicePollerController {

    private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
    UrlValidator urlValidator = new UrlValidator();
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
        if (!urlValidator.isValid(newService.getUrl())) {
            throw new IllegalArgumentException("Invalid url provided: " + newService.getUrl());
        }
        return this.service
                .create(newService.getName(), newService.getUrl());
    }

    @DeleteMapping("/{id}")
    Publisher<Service> deleteById(@PathVariable String id) {
        return this.service.delete(id);
    }

    @PutMapping("/{id}")
    Publisher<ResponseEntity<Service>> updateById(@PathVariable String id, @RequestBody NewServiceDTO service) {
        if (!urlValidator.isValid(service.getUrl())) {
            throw new IllegalArgumentException("Invalid url provided: " + service.getUrl());
        }
        return Mono
                .just(service)
                .flatMap(s -> this.service.update(id, s.getName(), s.getUrl()))
                .map(p -> ResponseEntity
                        .ok()
                        .contentType(this.mediaType)
                        .build());
    }
}
