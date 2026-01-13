package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.UserRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserRecord, Long> {
    // CrudRepository.findById() returns Optional<UserRecord>
    // Don't override it with a custom method that returns UserRecord directly
}