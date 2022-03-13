import com.servicepoller.servicepoller.entity.Service;
import com.servicepoller.servicepoller.entity.Status;
import com.servicepoller.servicepoller.repository.ServiceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.UUID;

@Log4j2
@Component
@Profile("demo")
class SampleDataInitializer
        implements ApplicationListener<ApplicationReadyEvent> {

    private final ServiceRepository repository;

    public SampleDataInitializer(ServiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        repository
                .deleteAll()
                .thenMany(
                        Flux
                        .just("A", "B")
                        .map(name -> new Service(UUID.randomUUID().toString(), name + "@email.com", "www.google.com", LocalDateTime.now(), Status.UNKNOWN))
                        .flatMap(repository::save)
                )
                .thenMany(repository.findAll())
                .subscribe(profile -> log.info("saving " + profile.toString()));
    }
}