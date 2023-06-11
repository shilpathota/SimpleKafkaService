- Create a Spring boot starter project with dependencies Spring-Kafka, Spring Starter Web
- Create a configuration folder where we create new topic, configure the bootstrap servers or create kafkatemplate bean

```java
@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic inventoryTopic() {
        return TopicBuilder.name(AppConstants.TOPIC_NAME)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
```

- Create 2 services KafkaProducer and KafkaConsumer which has consume and send messages

```java
@Service
public class KafkaProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message){
        LOGGER.info(String.format("Message sent -> %s", message));
        kafkaTemplate.send(AppConstants.TOPIC_NAME, message);
    }
}
```

```java
@Service
public class KafkaConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = AppConstants.TOPIC_NAME,
                    groupId = AppConstants.GROUP_ID)
    public void consume(String message){
        LOGGER.info(String.format("Message received -> %s", message));
    }
}
```

- In [application.properties](http://application.properties), we configure the configuration as follows

```java
spring.kafka.consumer.bootstrap-servers= localhost:29092
spring.kafka.consumer.group-id= consumer-group1
spring.kafka.consumer.auto-offset-reset= earliest
spring.kafka.consumer.key-deserializer= org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer= org.apache.kafka.common.serialization.StringDeserializer

spring.kafka.producer.bootstrap-servers= localhost:29092
spring.kafka.producer.key-serializer= org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer= org.apache.kafka.common.serialization.StringSerializer

server.port=8866
```

- Now we create a controller class which exposes the API for publishing the messages which is KafkaProducerController

```java
@RestController
@RequestMapping("/api/v1/kafka")
public class KafkaProducerController {
    private KafkaProducer kafkaProducer;

    public KafkaProducerController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @GetMapping("/publish")
    public ResponseEntity<String> publish(@RequestParam("message") String message){
        kafkaProducer.sendMessage(message);
        return ResponseEntity.ok("Message sent to kafka topic");
    }
}
```

- We are ready to run the project before which we make sure that zookeeper and kafka is runnning on the ports configured in the properties
- Send the message in the API http://localhost:8866/api/v1/kafka/publish?message=Test Message
- we get the logs as

```java
2023-06-10T18:47:18.166-05:00  INFO 13264 --- [ad | producer-1] o.a.k.c.p.internals.TransactionManager   : [Producer clientId=producer-1] ProducerId set to 4000 with epoch 0
2023-06-10T18:47:18.946-05:00  INFO 13264 --- [ntainer#0-0-C-1] c.k.c.services.KafkaConsumer             : Message received -> Test Message
2023-06-10T18:47:22.694-05:00  INFO 13264 --- [ntainer#0-0-C-1] org.apache.kafka.clients.NetworkClient   : [Consumer clientId=consumer-group_id1-1, groupId=group_id1] Node -1 disconnected.
2023-06-10T18:56:48.152-05:00  INFO 13264 --- [ad | producer-1] org.apache.kafka.clients.NetworkClient   : [Producer clientId=producer-1] Node -1 disconnected.
```
