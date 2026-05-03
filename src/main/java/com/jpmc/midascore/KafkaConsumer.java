package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-consumer-group")
    public void listen(Transaction transaction) {
        // Just log the transaction - no processing needed for Task 1
        System.out.println("Received transaction: " + transaction);
    }
}