package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@DirtiesContext
class TaskTwoTests {
    static final Logger logger = LoggerFactory.getLogger(TaskTwoTests.class);

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private TransactionListener transactionListener;

    @Test
    void task_two_verifier() throws InterruptedException {
        // Load test data from file
        String[] transactionLines = fileLoader.loadStrings("/test_data/poiuytrewq.uiop");

        List<Float> amounts = new ArrayList<>();

        logger.info("==================================================");
        logger.info("TASK TWO: KAFKA LISTENER TEST");
        logger.info("==================================================");
        logger.info("Processing {} transactions...", transactionLines.length);
        logger.info("==================================================");

        // Simulate Kafka delivering each transaction
        int transactionNumber = 1;
        for (String transactionLine : transactionLines) {
            // Parse transaction data: "senderId, recipientId, amount"
            String[] parts = transactionLine.split(", ");
            long senderId = Long.parseLong(parts[0]);
            long recipientId = Long.parseLong(parts[1]);
            float amount = Float.parseFloat(parts[2]);

            Transaction transaction = new Transaction(senderId, recipientId, amount);

            logger.info("");
            logger.info(">>> TRANSACTION {} INCOMING <<<", transactionNumber);
            logger.info("Data: senderId={}, recipientId={}, amount={}", senderId, recipientId, amount);

            // Simulate Kafka listener receiving the message
            transactionListener.listen(transaction);

            // Record the amount
            amounts.add(amount);
            logger.info(">>> AMOUNT RECORDED: {} <<<", amount);

            transactionNumber++;
            Thread.sleep(500);
        }

        // Summary
        logger.info("");
        logger.info("==================================================");
        logger.info("RESULTS: FIRST 4 TRANSACTION AMOUNTS");
        logger.info("==================================================");
        for (int i = 0; i < amounts.size(); i++) {
            logger.info("Transaction {}: {}", (i + 1), amounts.get(i));
        }
        logger.info("==================================================");
        logger.info("SUBMIT THESE 4 AMOUNTS TO FORAGE:");
        logger.info("  1. " + amounts.get(0));
        logger.info("  2. " + amounts.get(1));
        logger.info("  3. " + amounts.get(2));
        logger.info("  4. " + amounts.get(3));
        logger.info("==================================================");
    }
}