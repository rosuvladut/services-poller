package com.servicepoller.servicepoller.events;

import com.servicepoller.servicepoller.entity.Service;
import org.springframework.context.ApplicationEvent;

public class ServiceEvent extends ApplicationEvent {
    public ServiceEvent(Service service) {
        super(service);
    }
}
