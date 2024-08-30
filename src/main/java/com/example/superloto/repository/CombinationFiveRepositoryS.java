package com.example.superloto.repository;

import com.example.superloto.entity.CombinationFive;
import com.example.superloto.entity.CombinationFiveS;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CombinationFiveRepositoryS extends MongoRepository<CombinationFiveS, String> {
    CombinationFiveS findByNumbers(String numbers);

    List<CombinationFiveS> findAllByNumbersIn(List<String> numbers);
}
