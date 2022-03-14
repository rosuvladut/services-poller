package com.servicepoller.servicepoller.controller;

import com.servicepoller.servicepoller.dto.NewServiceDTO;
import com.servicepoller.servicepoller.entity.Service;
import com.servicepoller.servicepoller.entity.Status;
import com.servicepoller.servicepoller.repository.ServiceRepository;
import com.servicepoller.servicepoller.service.ServicesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ServicePollerController.class)
class ServicePollerControllerTest {

    @MockBean
    ServiceRepository serviceRepository;

    @MockBean
    ServicesService servicesService;

    @Autowired
    private WebTestClient webClient;

    @Test
    void testGetServices() {
        Service service = new Service();
        service.setId("111");
        service.setName("Test");
        service.setUrl("http://www.test.com");

        List<Service> list = new ArrayList<>();
        list.add(service);

        Flux<Service> employeeFlux = Flux.fromIterable(list);

        Mockito.when(servicesService.all())
                .thenReturn(employeeFlux);

        webClient.get().uri("/services/")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Service.class);

        Mockito.verify(servicesService, times(1)).all();
    }

    @Test
    void testGetServiceById() {
        Service service = new Service();
        service.setId("111");
        service.setName("Test");
        service.setUrl("http://www.test.com");

        Mockito
                .when(servicesService.get("111"))
                .thenReturn(Mono.just(service));

        webClient.get().uri("/services/{id}", 111)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isNotEmpty()
                .jsonPath("$.id").isEqualTo("111")
                .jsonPath("$.name").isEqualTo("Test")
                .jsonPath("$.url").isEqualTo("http://www.test.com");

        Mockito.verify(servicesService, times(1)).get("111");
    }

    @Test
    void testCreateService() {
        NewServiceDTO newServiceDTO = new NewServiceDTO();
        newServiceDTO.setName("New Test Service");
        newServiceDTO.setUrl("http://www.test.com");

        Service service = new Service();
        service.setId("111");
        service.setName("New Test Service");
        service.setUrl("http://www.test.com");
        service.setCreatedOn(LocalDateTime.now());
        service.setStatus(Status.UNKNOWN);

        Mockito.when(servicesService.create(newServiceDTO.getName(), newServiceDTO.getUrl())).thenReturn(Mono.just(service));

        webClient.post()
                .uri("/services")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(newServiceDTO))
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(servicesService, times(1)).create(newServiceDTO.getName(), newServiceDTO.getUrl());
    }

    @Test
    void testCreateServiceWithInvalidURLThrowsException() {
        NewServiceDTO newServiceDTO = new NewServiceDTO();
        newServiceDTO.setName("New Test Service");
        newServiceDTO.setUrl("testInvalid.com");

        webClient.post()
                .uri("/services")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(newServiceDTO))
                .exchange()
                .expectStatus().is5xxServerError();

        Mockito.verify(servicesService, times(0)).create(newServiceDTO.getName(), newServiceDTO.getUrl());
    }

    @Test
    void testUpdateService() {
        NewServiceDTO newServiceDTO = new NewServiceDTO();
        newServiceDTO.setName("New Test Service");
        newServiceDTO.setUrl("http://www.test.com");

        Service service = new Service();
        service.setId("111");
        service.setName("New Test Service");
        service.setUrl("http://www.test.com");
        service.setCreatedOn(LocalDateTime.now());
        service.setStatus(Status.UNKNOWN);

        Mockito.when(servicesService.update("111", newServiceDTO.getName(), newServiceDTO.getUrl())).thenReturn(Mono.just(service));

        webClient.put()
                .uri("/services/{id}", 111)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(newServiceDTO))
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(servicesService, times(1)).update("111", newServiceDTO.getName(), newServiceDTO.getUrl());
    }

    @Test
    void testDeleteService() {
        Service service = new Service();
        service.setId("111");
        service.setName("New Test Service");
        service.setUrl("http://www.test.com");
        Mockito
                .when(servicesService.delete("111"))
                .thenReturn(Mono.just(service));

        webClient.delete().uri("/services/{id}", 111)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Service.class);
        Mockito.verify(servicesService, times(1)).delete("111");
    }

}