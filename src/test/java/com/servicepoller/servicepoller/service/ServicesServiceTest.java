package com.servicepoller.servicepoller.service;

import com.servicepoller.servicepoller.entity.Service;
import com.servicepoller.servicepoller.entity.Status;
import com.servicepoller.servicepoller.repository.ServiceRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Predicate;

@Log4j2
@DataMongoTest
@Import(ServicesService.class)
@TestPropertySource(properties = "spring.mongodb.embedded.version=4.0.2")
public class ServicesServiceTest {

    private final ServicesService service;
    private final ServiceRepository repository;

    public ServicesServiceTest(@Autowired ServicesService service, @Autowired ServiceRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @Test
    public void getAll() {
        Flux<Service> saved = repository.saveAll(Flux.just(
                new Service(null, "Service 1", "www.service1.com", LocalDateTime.now(), Status.UNKNOWN),
                new Service(null, "Service 2", "www.service2.com", LocalDateTime.now(), Status.UNKNOWN),
                new Service(null, "Service 3", "www.service3.com", LocalDateTime.now(), Status.UNKNOWN)));

        Flux<Service> composite = service.all().thenMany(saved);

        Predicate<Service> match = service -> saved.any(saveItem -> saveItem.equals(service)).block();

        StepVerifier
                .create(composite)
                .expectNextMatches(match)
                .expectNextMatches(match)
                .expectNextMatches(match)
                .verifyComplete();
    }

    @Test
    public void save() {
        Mono<Service> serviceMono = this.service.create("Service 1", "www.service1.com");
        StepVerifier
                .create(serviceMono)
                .expectNextMatches(saved -> StringUtils.hasText(saved.getId()) && StringUtils.hasText(saved.getUrl()))
                .verifyComplete();
    }

    @Test
    public void delete() {
        String test = "test";
        Mono<Service> deleted = this.service
                .create(test, "www.test.com")
                .flatMap(saved -> this.service.delete(saved.getId()));
        StepVerifier
                .create(deleted)
                .expectNextMatches(service -> service.getName().equalsIgnoreCase(test))
                .verifyComplete();
    }

    @Test
    public void update() {
        Mono<Service> saved = this.service
                .create("test", "www.test.com")
                .flatMap(p -> this.service.update(p.getId(), "test1", "www.test1.com"));
        StepVerifier
                .create(saved)
                .expectNextMatches(service1 -> service1.getName().equalsIgnoreCase("test1") &&
                        service1.getUrl().equalsIgnoreCase("www.test1.com"))
                .verifyComplete();
    }

    @Test
    public void getById() {
        String test = UUID.randomUUID().toString();
        Mono<Service> deleted = this.service
                .create(test, "www.google.com")
                .flatMap(saved -> this.service.get(saved.getId()));
        StepVerifier
                .create(deleted)
                .expectNextMatches(service -> StringUtils.hasText(service.getId()) && test.equalsIgnoreCase(service.getName()))
                .verifyComplete();
    }
}