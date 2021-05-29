# Event-driven microservices with Kafka, Elasticsearch and PostgreSQL
## Services Overview

### Data Ingestion Services
- **twitter-to-kafka-service**: Streams data from Twitter4J API into Kafka under topic `twitter-topic`.
- **kafka-to-elastic-service**: Consumes `twitter-topic` from Kafka into Elasticsearch
- **kafka-streams-service**: Streams `twitter-topic` and outputs word count into another Kafka topic `twitter-analytics-topic`.
- **analytics-service**: Consumes `twitter-analytics-topic` and batch-inserts into PostgreSQL; Exposes `/get-word-count-by-word` API to client-facing services.

### Client-facing Services
- **elastic-query-web-client**: Web client for querying Elasticsearch for tweets, as well as analytics-service for word count.
- **elastic-query-service**: The service responding to requests from elastic-query-web-client.

### Traffic, Configuration, and Security
- **discovery-service**: A Netflix Eureka server to facilitate service discovery and automatic client-side load balancing.
- **gateway-service**: An API gateway with a circuit breaker, and a Redis rate limiter.
- **config-server**: Facilitates externalized configurations. Backed by `config-server-repository`. Sensitive configuration is encrypted with JCE.
- **keycloak-authorization-server**: Facilitates authorization/authentication using OAuth2 and OpenID. Enables Single Sign-on.

### Monitoring
- **Prometheus/Grafana**: For monitoring dashboards.
- **ELK-stack** and **Sleuth/Zipkin**: For distributed tracing, log aggregation and visualization.
