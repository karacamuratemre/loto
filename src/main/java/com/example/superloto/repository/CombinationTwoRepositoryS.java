package com.example.superloto.repository;

import com.example.superloto.entity.CombinationTwo;
import com.example.superloto.entity.CombinationTwoS;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CombinationTwoRepositoryS extends MongoRepository<CombinationTwoS, String> {
    CombinationTwoS findByNumbers(String numbers);

    List<CombinationTwoS> findAllByNumbersIn(List<String> numbers);

}