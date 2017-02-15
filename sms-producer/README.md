# Sms Production Service

The service produces `io.openexchange.pojos.SMS` and sends them away into bind Spring Cloud Bus.   

## Bootstrap modes supported:

- spring-boot
- Cloudfoundry
- Docker

## Properties

| Name | Default value | Description | 
| --- | --- | --- |
| server.port | 8085 | A local bind port |
| spring.producer.concurrency | 1 | The number of parallel producers |

## Docker

When it's been bootstrapped with __docker__ the following environment variable must be specified

|Name|Mandatory|Description|
|---|:---:|---|
|**JAVA_OPTS**|x|Contains JVM system properties. At least empty string must be provided.| 
 
 