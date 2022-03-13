package com.servicepoller.servicepoller.service;

import com.servicepoller.servicepoller.entity.Status;
import com.servicepoller.servicepoller.events.ServiceEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@EnableScheduling
@Log4j2
public class ServicePoller {

    private final WebClient webClient;
    private final ServicesService servicesService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ServicePoller(WebClient.Builder webClientBuilder, ServicesService servicesService, ApplicationEventPublisher applicationEventPublisher) {
        this.webClient = webClientBuilder.build();
        this.applicationEventPublisher = applicationEventPublisher;
        this.servicesService = servicesService;
    }

    @Scheduled(fixedDelay = 30000, initialDelay = 5000)
    public void servicesUpdateJob() {
        Flux<com.servicepoller.servicepoller.entity.Service> allServices = servicesService.all();
        allServices.flatMap(service -> webClient.get()
                        .uri(service.getUrl())
                        .exchange()
                        .map(ClientResponse::statusCode)
                        .flatMap(httpStatus -> {
                                    log.info(service.getUrl() + " " + httpStatus);
                                    if (httpStatus.is2xxSuccessful() && (Status.UP != service.getStatus())) {
                                        applicationEventPublisher.publishEvent(createServiceEvent(service, Status.UP));
                                        return servicesService.updateStatus(service.getId(), Status.UP);
                                    } else if (!httpStatus.is2xxSuccessful() && (Status.DOWN != service.getStatus())) {
                                        applicationEventPublisher.publishEvent(createServiceEvent(service, Status.DOWN));
                                        return servicesService.updateStatus(service.getId(), Status.DOWN);
                                    } else return Mono.empty();
                                }
                        )
                ).doOnNext(service -> log.info(service.getName()))
                .subscribe();
    }

    private ServiceEvent createServiceEvent(com.servicepoller.servicepoller.entity.Service service, Status status) {
        return new ServiceEvent(service.toBuilder().status(status).build());
    }
}
