# Sms Consumption Service

Test service to verify concurrency aspects of JMS/JPA combination from the consumption aspects

## Bootstrap modes supported:

- spring-boot
- Cloudfoundry
- Docker

## Properties

| Name | Default value | Description | 
| --- | --- | --- |
| **server.port** | _8084_ | A local bind port |
| **sms.outbound.queue.read.chunk.size** | 100 | The most number of messages consumed ber a turn  |
| **spring.consumers.concurrency** | The number of available processor's cores | The number of parallel consumers |

## Docker

When run with __docker__ user following environment variable:

|Name|Mandatory|Description|
|---|:---:|---|
|**JAVA_OPTS**|x|Contains JVM system properties. At least empty string must be provided.|