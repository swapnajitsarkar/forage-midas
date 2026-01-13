package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class TaskFiveTests {
    static final Logger logger = LoggerFactory.getLogger(TaskFiveTests.class);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private TransactionListener transactionListener;

    @BeforeEach
    void setUp() {
        userPopulator.populate();
    }

    @Test
    void task_five_verifier() throws InterruptedException {
        String[] transactionLines = fileLoader.loadStrings("/test_data/poiuytrewq.uiop");

        logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        logger.info("BEGIN");
        logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        logger.info("TASK FIVE: REST API BALANCE ENDPOINT TEST");
        logger.info("");
        logger.info("Processing {} transactions with Incentive API...", transactionLines.length);

        // Process each transaction
        for (String transactionLine : transactionLines) {
            String[] parts = transactionLine.split(", ");
            long senderId = Long.parseLong(parts[0]);
            long recipientId = Long.parseLong(parts[1]);
            float amount = Float.parseFloat(parts[2]);

            Transaction transaction = new Transaction(senderId, recipientId, amount);

            logger.info("Processing: Sender={}, Recipient={}, Amount={}",
                    senderId, recipientId, amount);

            transactionListener.listen(transaction);

            Thread.sleep(500);
        }

        Thread.sleep(2000);

        logger.info("");
        logger.info("All transactions processed.");
        logger.info("");
        logger.info("Querying balance via REST API endpoint...");
        logger.info("");

        // Query balance via REST API
        ResponseEntity<Balance> response = restTemplate.getForEntity("/balance?userId=2", Balance.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Balance balance = response.getBody();
            float wilburBalance = balance.getAmount();

            logger.info("User ID: 2");
            logger.info("User Name: wilbur");
            logger.info("Final Balance: {}", wilburBalance);
            logger.info("Rounded Down: {}", Math.floor(wilburBalance));
            logger.info("");
            logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            logger.info("END");
            logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        } else {
            logger.error("Failed to query balance via REST API");
            logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            logger.info("END");
            logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        }
    }
}