package com.microservices.demo.discovery.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer // to make this application a registration and discovery service
@SpringBootApplication
public class ServiceRegistrationAndDiscoveryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistrationAndDiscoveryServiceApplication.class, args);
    }
}
