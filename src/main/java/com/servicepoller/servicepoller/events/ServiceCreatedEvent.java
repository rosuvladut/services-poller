package com.servicepoller.servicepoller.events;

import com.servicepoller.servicepoller.entity.Service;
import org.springframework.context.ApplicationEvent;

public class ServiceCreatedEvent extends ApplicationEvent {
    public ServiceCreatedEvent(Service service) {
        super(service);
    }
}
