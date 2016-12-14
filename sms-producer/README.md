# Sms Production Service

Test service to verify concurrency aspects of JMS/JPA combination from the consumption messages producing aspects

## Properties

| Name | Default value | Description | 
| --- | --- | --- |
| server.port | 8085 | A local bind port |
| sms.outbound.queue.write.chunk.size | 100 | The most number of messages produced ber a turn  |
| spring.producer.concurrency | 1 | The number of parallel producers |

     