package com.servicepoller.servicepoller;

import com.servicepoller.servicepoller.events.ServiceEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Component
public class ServiceEventPublisher implements
        ApplicationListener<ServiceEvent>,
        Consumer<FluxSink<ServiceEvent>> {

    private final Executor executor;
    private final BlockingQueue<ServiceEvent> queue =
            new LinkedBlockingQueue<>();

    ServiceEventPublisher(Executor executor) {
        this.executor = executor;
    }


    @Override
    public void onApplicationEvent(ServiceEvent event) {
        this.queue.offer(event);
    }

    @Override
    public void accept(FluxSink<ServiceEvent> sink) {
        this.executor.execute(() -> {
            while (true)
                try {
                    ServiceEvent event = queue.take();
                    sink.next(event);
                } catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
        });

    }
}
