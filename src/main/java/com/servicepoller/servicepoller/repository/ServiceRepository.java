package com.servicepoller.servicepoller.repository;

import com.servicepoller.servicepoller.entity.Service;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ServiceRepository extends ReactiveMongoRepository<Service, String> {
}
