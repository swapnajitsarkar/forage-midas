package com.jpmc.midascore;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/balance")
@Slf4j
public class BalanceController {

    @Autowired
    private DatabaseConduit databaseConduit;

    @GetMapping
    public Balance getBalance(@RequestParam Long userId) {
        log.info("Balance query for user: {}", userId);
        Optional<UserRecord> userOpt = databaseConduit.getUserBalance(userId);

        if (userOpt.isPresent()) {
            return new Balance(userOpt.get().getBalance());
        }

        return new Balance(0f);
    }
}
