package com.jpmc.midascore;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class KafkaConsumer {

    @Autowired
    private DatabaseConduit databaseConduit;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-consumer-group")
    @Transactional
    public void listen(Transaction transaction) {
        System.out.println("Processing transaction: " + transaction);
        
        // Get sender and recipient
        UserRecord sender = databaseConduit.findUserById(transaction.getSenderId());
        UserRecord recipient = databaseConduit.findUserById(transaction.getRecipientId());
        
        if (sender == null || recipient == null) {
            System.out.println("User not found!");
            return;
        }
        
        // Check if sender has enough balance
        if (sender.getBalance() >= transaction.getAmount()) {
            // Deduct from sender
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            databaseConduit.save(sender);
            
            // Add to recipient
            recipient.setBalance(recipient.getBalance() + transaction.getAmount());
            databaseConduit.save(recipient);
            
            System.out.println("Transaction processed successfully!");
            System.out.println("Sender " + sender.getName() + " new balance: " + sender.getBalance());
            System.out.println("Recipient " + recipient.getName() + " new balance: " + recipient.getBalance());
        } else {
            System.out.println("Insufficient funds! Sender balance: " + sender.getBalance());
        }
    }
}