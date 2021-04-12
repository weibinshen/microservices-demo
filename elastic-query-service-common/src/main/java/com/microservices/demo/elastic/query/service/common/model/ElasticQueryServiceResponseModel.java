package com.microservices.demo.elastic.query.service.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@Builder
// Spring requires a no-arg constructor to deserialize JSON to java object
// Spring will first create the java object with the no-arg constructor, and then populate the value from JSON.
@NoArgsConstructor
// Builder lombok annotation requires All-args constructor. Typically we do not need to call out this specifically.
// However, we need to specifically call-out AllArgsConstructor here because we are using NoArgsConstructor above.
@AllArgsConstructor
// extending RepresentationModel will change the response object to a REST representation model so we can set Hateoas links on it.
public class ElasticQueryServiceResponseModel extends RepresentationModel<ElasticQueryServiceResponseModel> {
    private String id;
    private Long userId;
    private String text;
    private LocalDateTime createdAt;
}
