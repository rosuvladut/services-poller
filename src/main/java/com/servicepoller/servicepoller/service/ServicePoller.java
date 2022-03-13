package com.servicepoller.servicepoller.service;

import com.servicepoller.servicepoller.entity.Status;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@EnableScheduling
@Log4j2
public class ServicePoller {

    private final WebClient webClient;
    private final RestTemplate restTemplate;
    private final ServicesService servicesService;

    public ServicePoller(WebClient.Builder webClientBuilder, ServicesService servicesService) {
        this.webClient = webClientBuilder.build();
        this.restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
        this.servicesService = servicesService;
    }

    @Scheduled(fixedDelay = 50000)
    public void servicesUpdateJob() {
        Flux<com.servicepoller.servicepoller.entity.Service> allServices = servicesService.all();
        allServices.flatMap(service -> webClient.get()
                        .uri("http://" + service.getUrl())
                        .exchange()
                        .map(ClientResponse::statusCode)
                        .flatMap(httpStatus -> {
                                    log.info(service.getUrl() + " " + httpStatus);
                                    if (httpStatus.is2xxSuccessful()) {
                                        return servicesService.updateStatus(service.getId(), service.getName(), service.getUrl(), Status.UP);
                                    } else {
                                        return servicesService.updateStatus(service.getId(), service.getName(), service.getUrl(), Status.DOWN);
                                    }
                                }
                        )
                ).doOnNext(service -> log.info(service.getName()))
                .subscribe();
    }


    //    @Scheduled(fixedDelay = 50000)
    public void pollJob() throws URISyntaxException {
        Flux<com.servicepoller.servicepoller.entity.Service> allServices = servicesService.all();
        List<com.servicepoller.servicepoller.entity.Service> serviceList = allServices.collectList().block();
        serviceList.forEach(service -> {
            String url = "http://" + service.getUrl();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                servicesService.updateStatus(service.getId(), service.getName(), service.getUrl(), Status.UNKNOWN).block();
            } else {
                servicesService.updateStatus(service.getId(), service.getName(), service.getUrl(), Status.UNKNOWN).block();
            }
        });
        log.info(LocalDateTime.now());
    }

}
