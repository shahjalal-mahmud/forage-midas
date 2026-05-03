package com.jpmc.midascore;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
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
        
        // Validate sender and recipient exist
        UserRecord sender = databaseConduit.findUserById(transaction.getSenderId());
        UserRecord recipient = databaseConduit.findUserById(transaction.getRecipientId());
        
        if (sender == null) {
            System.out.println("REJECTED: Sender ID " + transaction.getSenderId() + " not found");
            return;
        }
        
        if (recipient == null) {
            System.out.println("REJECTED: Recipient ID " + transaction.getRecipientId() + " not found");
            return;
        }
        
        // Check if sender has sufficient balance
        if (sender.getBalance() >= transaction.getAmount()) {
            // Update balances
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount());
            
            // Save updated users
            databaseConduit.save(sender);
            databaseConduit.save(recipient);
            
            // Create and save transaction record
            TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount());
            databaseConduit.save(transactionRecord);
            
            System.out.println("ACCEPTED: Transaction processed successfully!");
            System.out.println("Sender " + sender.getName() + " new balance: " + sender.getBalance());
            System.out.println("Recipient " + recipient.getName() + " new balance: " + recipient.getBalance());
        } else {
            System.out.println("REJECTED: Insufficient funds! Sender balance: " + sender.getBalance() + 
                             ", Required: " + transaction.getAmount());
        }
    }
}