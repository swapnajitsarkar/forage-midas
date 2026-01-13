package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class DatabaseConduit {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    @Transactional
    public void processTransaction(Transaction transaction) {
        log.info("=== PROCESSING TRANSACTION ===");
        log.info("Sender: {}, Recipient: {}, Amount: {}",
                transaction.getSenderId(), transaction.getRecipientId(), transaction.getAmount());

        // VALIDATION 1: Check sender exists
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        if (sender == null) {
            log.warn("INVALID: Sender ID {} not found", transaction.getSenderId());
            return;
        }

        // VALIDATION 2: Check recipient exists
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        if (recipient == null) {
            log.warn("INVALID: Recipient ID {} not found", transaction.getRecipientId());
            return;
        }

        float amount = transaction.getAmount();

        // VALIDATION 3: Check sender has sufficient balance
        if (sender.getBalance() < amount) {
            log.warn("INVALID: Insufficient balance. Sender: {}, Required: {}",
                    sender.getBalance(), amount);
            return;
        }

        // ALL VALIDATIONS PASSED - PROCESS TRANSACTION
        log.info("VALID: All validations passed. Processing transaction...");

        // Update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        // Save updated user balances
        userRepository.save(sender);
        userRepository.save(recipient);

        // Record transaction to database
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount);
        transactionRepository.save(transactionRecord);

        log.info("=== TRANSACTION COMPLETED ===");
        log.info("Sender new balance: {}", sender.getBalance());
        log.info("Recipient new balance: {}", recipient.getBalance());
    }

    public UserRecord getUserBalance(long userId) {
        return userRepository.findById(userId);
    }
}
