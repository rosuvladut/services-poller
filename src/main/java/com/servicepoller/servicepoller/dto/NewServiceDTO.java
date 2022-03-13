package com.servicepoller.servicepoller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewServiceDTO {

    @JsonProperty(value = "Name")
    String Name;
    @JsonProperty(value = "Url")
    String Url;

}
