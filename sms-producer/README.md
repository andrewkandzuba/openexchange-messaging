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
| openexchange.sms.producer.job.parallelism | 4 | The number of parallel producers |
| openexchange.sms.producer.job.repeatInterval | 10 | The repeat rate |
| openexchange.sms.producer.job.repeatIntervalTimeUnit | MINUTES | The repeat rate time units |

## Docker

When it's been bootstrapped with __docker__ the following environment variable must be specified

|Name|Mandatory|Description|
|---|:---:|---|
|**JAVA_OPTS**|x|Contains JVM system properties. At least empty string must be provided.| 
 
 