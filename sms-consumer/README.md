# Sms Consumption Service

Test service to verify concurrency aspects of JMS/JPA combination from the consumption aspects

## Properties

| Name | Default value | Description | 
| --- | --- | --- |
| server.port | 8084 | A local bind port |
| sms.outbound.queue.read.chunk.size | 100 | The most number of messages consumed ber a turn  |
| spring.consumers.concurrency | The number of available processor's cores | The number of parallel consumers |
     