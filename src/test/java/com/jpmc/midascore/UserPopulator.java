package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserPopulator {

    @Autowired
    private UserRepository userRepository;

    public void populate() {
        // Clear existing users (in case of re-runs)
        userRepository.deleteAll();

        // Create users with specific IDs matching transaction test data
        userRepository.save(new UserRecord(1, "waldorf", 1000f));
        userRepository.save(new UserRecord(2, "statler", 500f));
        userRepository.save(new UserRecord(3, "bernie", 300f));
        userRepository.save(new UserRecord(4, "foo", 200f));
        userRepository.save(new UserRecord(5, "bar", 150f));
        userRepository.save(new UserRecord(6, "baz", 100f));
        userRepository.save(new UserRecord(7, "qux", 75f));
        userRepository.save(new UserRecord(8, "corge", 50f));
    }
}
