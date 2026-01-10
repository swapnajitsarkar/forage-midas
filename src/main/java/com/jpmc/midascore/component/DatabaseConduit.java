package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Slf4j
public class DatabaseConduit {

    @Autowired
    private UserRepository userRepository;

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    @Transactional
    public void processTransaction(Transaction transaction) {
        log.info("Processing transaction - Sender: {}, Recipient: {}, Amount: {}",
                transaction.getSenderId(), transaction.getRecipientId(), transaction.getAmount());

        // Convert long to Long for findById
        Optional<UserRecord> senderOpt = userRepository.findById(Long.valueOf(transaction.getSenderId()));
        Optional<UserRecord> recipientOpt = userRepository.findById(Long.valueOf(transaction.getRecipientId()));

        if (!senderOpt.isPresent() || !recipientOpt.isPresent()) {
            log.warn("Sender or recipient not found!");
            return;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();
        float amount = transaction.getAmount();

        if (sender.getBalance() < amount) {
            log.warn("Insufficient balance!");
            return;
        }

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        userRepository.save(sender);
        userRepository.save(recipient);

        log.info("Transaction completed successfully");
    }

    public Optional<UserRecord> getUserBalance(long userId) {
        return userRepository.findById(Long.valueOf(userId));
    }
}
