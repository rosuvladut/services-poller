package com.servicepoller.servicepoller;

import com.servicepoller.servicepoller.entity.Service;
import com.servicepoller.servicepoller.entity.Status;
import com.servicepoller.servicepoller.repository.ServiceRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Log4j2
@WebFluxTest
public abstract class AbstractBaseProfileEndpoints {

    private final WebTestClient client;

    @MockBean
    private ServiceRepository repository;

    public AbstractBaseProfileEndpoints(WebTestClient client) {
        this.client = client;
    }

    @Test
    public void getAll() {

        log.info("running  " + this.getClass().getName());


        Mockito
                .when(this.repository.findAll())
                .thenReturn(Flux.just(new Service("1", "A", "www.aaa.com", LocalDateTime.now(), Status.UNKNOWN),
                        new Service("2", "B", "www.bbbb.com", LocalDateTime.now(),Status.UNKNOWN)));


        this.client
                .get()
                .uri("/services")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo("1")
                .jsonPath("$.[0].name").isEqualTo("A")
                .jsonPath("$.[1].id").isEqualTo("2")
                .jsonPath("$.[1].name").isEqualTo("B");
    }

    @Test
    public void save() {
        Service data = new Service("123", "name", "www.test.com", LocalDateTime.now(), Status.UNKNOWN);
        Mockito
                .when(this.repository.save(Mockito.any(Service.class)))
                .thenReturn(Mono.just(data));
        MediaType jsonUtf8 = MediaType.APPLICATION_JSON_UTF8;
        this
                .client
                .post()
                .uri("/services")
                .contentType(jsonUtf8)
                .body(Mono.just(data), Service.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(jsonUtf8);
    }

    @Test
    public void delete() {
        Service data = new Service("123", "aaaa", "test.com", LocalDateTime.now(),Status.UNKNOWN);
        Mockito
                .when(this.repository.findById(data.getId()))
                .thenReturn(Mono.just(data));
        Mockito
                .when(this.repository.deleteById(data.getId()))
                .thenReturn(Mono.empty());
        this
                .client
                .delete()
                .uri("/services/" + data.getId())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void update() {
        Service data = new Service("123", "aaaa", "bbbb.com", LocalDateTime.now(), Status.UNKNOWN);

        Mockito
                .when(this.repository.findById(data.getId()))
                .thenReturn(Mono.just(data));

        Mockito
                .when(this.repository.save(data))
                .thenReturn(Mono.just(data));

        this
                .client
                .put()
                .uri("/services/" + data.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(data), Service.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void getById() {

        Service data = new Service("1", "A", "www.aaa.com", LocalDateTime.now(), Status.UNKNOWN);

        Mockito
                .when(this.repository.findById(data.getId()))
                .thenReturn(Mono.just(data));

        this.client
                .get()
                .uri("/profiles/" + data.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.id").isEqualTo(data.getId())
                .jsonPath("$.email").isEqualTo(data.getName());
    }
}
