package com.jpmc.midascore;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.foundation.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionListener {

    @Autowired
    private DatabaseConduit databaseConduit;

    @Value("${general.kafka-topic}")
    private String kafkaTopic;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void listen(Transaction transaction) {
        log.info("=== RECEIVED TRANSACTION ===");
        log.info("Sender: {}, Recipient: {}, Amount: {}",
                transaction.getSenderId(),
                transaction.getRecipientId(),
                transaction.getAmount());

        try {
            databaseConduit.processTransaction(transaction);
            log.info("=== TRANSACTION PROCESSED ===");
        } catch (Exception e) {
            log.error("Error processing transaction", e);
        }
    }
}