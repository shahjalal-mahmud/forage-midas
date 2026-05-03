package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRepository;

    public DatabaseConduit(UserRepository userRepository, TransactionRecordRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }
    
    public void save(TransactionRecord transactionRecord) {
        transactionRepository.save(transactionRecord);
    }

    public Optional<UserRecord> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public UserRecord findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}