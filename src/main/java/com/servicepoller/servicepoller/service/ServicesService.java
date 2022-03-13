package com.servicepoller.servicepoller.service;

import com.servicepoller.servicepoller.entity.Service;
import com.servicepoller.servicepoller.entity.Status;
import com.servicepoller.servicepoller.events.ServiceCreatedEvent;
import com.servicepoller.servicepoller.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServicesService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ServiceRepository serviceRepository;

    public Flux<Service> all() {
        return serviceRepository.findAll();
    }

    public Mono<Service> get(String id) {
        return serviceRepository.findById(id);
    }

    public Mono<Service> create(String name, String url) {
        LocalDateTime createdOn = LocalDateTime.now();
        return serviceRepository.save(new Service(null, name, url, createdOn, Status.UNKNOWN))
                .doOnSuccess(service -> applicationEventPublisher.publishEvent(new ServiceCreatedEvent(service)));
    }

    public Mono<Service> update(String id, String name, String url) {
        return serviceRepository.findById(id)
                .map(service -> new Service(service.getId(), name, url, service.getCreatedOn(), service.getStatus()))
                .flatMap(serviceRepository::save);
    }

    public Mono<Service> updateStatus(String id, String name, String url, Status newStatus) {
        return serviceRepository.findById(id)
                .map(service -> new Service(service.getId(), name, url, service.getCreatedOn(), newStatus))
                .flatMap(serviceRepository::save);
    }

    public Mono<Service> delete(String id) {
        return serviceRepository.findById(id)
                .flatMap(service -> serviceRepository.deleteById(service.getId()).thenReturn(service));
    }

}
