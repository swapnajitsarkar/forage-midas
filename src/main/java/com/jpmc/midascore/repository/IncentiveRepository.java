package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.Incentive;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncentiveRepository extends CrudRepository<Incentive, Long> {
}
