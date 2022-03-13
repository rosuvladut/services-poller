package com.servicepoller.servicepoller;

import com.servicepoller.servicepoller.events.ServiceCreatedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Component
public class ServiceCreatedEventPublisher implements
        ApplicationListener<ServiceCreatedEvent>,
        Consumer<FluxSink<ServiceCreatedEvent>> {

    private final Executor executor;
    private final BlockingQueue<ServiceCreatedEvent> queue =
            new LinkedBlockingQueue<>();

    ServiceCreatedEventPublisher(Executor executor) {
        this.executor = executor;
    }


    @Override
    public void onApplicationEvent(ServiceCreatedEvent event) {
        this.queue.offer(event);
    }

    @Override
    public void accept(FluxSink<ServiceCreatedEvent> sink) {
        this.executor.execute(() -> {
            while (true)
                try {
                    ServiceCreatedEvent event = queue.take();
                    sink.next(event);
                } catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
        });

    }
}
