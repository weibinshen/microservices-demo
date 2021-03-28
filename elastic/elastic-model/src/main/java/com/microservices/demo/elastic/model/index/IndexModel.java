package com.microservices.demo.elastic.model.index;

public interface IndexModel {
    // we declare this method because we need to use it with the document template in ElasticIndexUtil.
    String getId();
}
