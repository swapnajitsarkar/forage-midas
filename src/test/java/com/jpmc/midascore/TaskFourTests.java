package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
class TaskFourTests {
    static final Logger logger = LoggerFactory.getLogger(TaskFourTests.class);

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
    void task_four_verifier() throws InterruptedException {
        String[] transactionLines = fileLoader.loadStrings("/test_data/poiuytrewq.uiop");

        logger.info("==================================================");
        logger.info("TASK FOUR: REST API INTEGRATION TEST");
        logger.info("==================================================");
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
        logger.info("==================================================");
        logger.info("WILBUR FINAL BALANCE");
        logger.info("==================================================");

        // Get wilbur (ID 2)
        UserRecord wilbur = userRepository.findById(2L).orElse(null);

        if (wilbur != null) {
            logger.info("User: {}", wilbur.getName());
            logger.info("Final Balance: {}", wilbur.getBalance());
            logger.info("Rounded Down: {}", Math.floor(wilbur.getBalance()));
            logger.info("==================================================");
            logger.info("SUBMIT THIS VALUE TO FORAGE: {}", (int)Math.floor(wilbur.getBalance()));
            logger.info("==================================================");
        }
    }
}