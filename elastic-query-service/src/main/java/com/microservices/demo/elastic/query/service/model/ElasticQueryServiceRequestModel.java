package com.microservices.demo.elastic.query.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryServiceRequestModel {
    private String id;
    @NotEmpty // NotEmpty = not null && length > 0
    private String text;
}

