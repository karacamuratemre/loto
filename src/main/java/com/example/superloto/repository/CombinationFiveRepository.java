package com.example.superloto.repository;

import com.example.superloto.entity.CombinationFive;
import com.example.superloto.entity.CombinationTwo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CombinationFiveRepository extends MongoRepository<CombinationFive, String> {
    CombinationFive findByNumbers(String numbers);

    List<CombinationFive> findAllByNumbersIn(List<String> numbers);
}
