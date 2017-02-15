# Sms Consumption Service

Test service to verify concurrency aspects of Spring Bus/JPA combination from the consumption aspects

## Bootstrap modes supported:

- spring-boot
- Cloudfoundry
- Docker

## Properties

| Name | Default value | Description | 
| --- | --- | --- |
| **server.port** | _8084_ | A local bind port |

## Docker

When it is has being run with __docker__ following environment variable must be set up

|Name|Mandatory|Description|
|---|:---:|---|
|**JAVA_OPTS**|x|Contains JVM system properties. At least empty string must be provided.|