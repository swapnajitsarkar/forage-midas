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
            // Parse JSON to Transaction object
            Transaction transaction = parseTransaction(transactionLine);

            logger.info("");
            logger.info(">>> TRANSACTION {} INCOMING <<<", transactionNumber);
            logger.info("JSON: {}", transactionLine);

            // Simulate Kafka listener receiving the message
            transactionListener.listen(transaction);

            // Record the amount
            float amount = transaction.getAmount();
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

    /**
     * Simple JSON parser to convert JSON string to Transaction object
     * Handles format: {"senderId": X, "recipientId": Y, "amount": Z}
     */
    private Transaction parseTransaction(String json) {
        // Remove braces
        json = json.replace("{", "").replace("}", "").trim();

        long senderId = 0;
        long recipientId = 0;
        float amount = 0;

        // Split by comma and parse key-value pairs
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("\"", "");
                String value = keyValue[1].trim().replaceAll("\"", "");

                switch (key) {
                    case "senderId":
                        senderId = Long.parseLong(value);
                        break;
                    case "recipientId":
                        recipientId = Long.parseLong(value);
                        break;
                    case "amount":
                        amount = Float.parseFloat(value);
                        break;
                }
            }
        }

        return new Transaction(senderId, recipientId, amount);
    }
}
