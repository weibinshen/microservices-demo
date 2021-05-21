package com.microservices.demo.mdc;

// MDC stands for mapped diagnostic context, which adds additional information to the log messages.

public class Constants {
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String CORRELATION_ID_KEY = "correlationID";
}
