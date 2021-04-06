package com.microservices.demo.elastic.query.service.model;

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
public class ElasticQueryServiceResponseModelV2 extends RepresentationModel<ElasticQueryServiceResponseModelV2> {
    private Long id; // In V2 we changed ID from String to Long, which is a breaking change
    private Long userId;
    private String text;
    private String text2; // We also removed createdAt and added text2, though for simplicity this new field is not adopted in ElasticDocuemntController
}
