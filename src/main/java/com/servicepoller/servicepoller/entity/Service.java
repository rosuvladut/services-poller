package com.servicepoller.servicepoller.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Service {

    @Id
    private String id;

    private String name;

    private String url;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime createdOn;

    private Status status;
}
