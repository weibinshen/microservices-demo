package com.microservices.demo.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.IdGenerator;
import org.springframework.util.JdkIdGenerator;

@Configuration
public class IdGeneratorConfig {
    // By using a UUID generator, we generate the IDs before we feed the record to DB
    // So batching can actually work in this case.
    // Without the UUID generator, we cannot really use batching.
    @Bean
    public IdGenerator idGenerator() {
        return new JdkIdGenerator();
    }
}