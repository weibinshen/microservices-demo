package com.microservices.demo.elastic.query.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
// Spring requires a no-arg constructor to deserialize JSON to java object
// Spring will first create the java object with the no-arg constructor, and then populate the value from JSON.
@NoArgsConstructor
// Builder lombok annotation requires All-args constructor. Typically we do not need to call out this specifically.
// However, we need to specifically call-out AllArgsConstructor here because we are using NoArgsConstructor above.
@AllArgsConstructor
public class ElasticQueryServiceResponseModel {
    private String id;
    private Long userId;
    private String text;
    private LocalDateTime createdAt;
}
