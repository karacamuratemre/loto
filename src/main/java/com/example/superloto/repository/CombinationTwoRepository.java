package com.example.superloto.repository;

import com.example.superloto.entity.CombinationOne;
import com.example.superloto.entity.CombinationTwo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CombinationTwoRepository  extends MongoRepository<CombinationTwo, String> {
    CombinationTwo findByNumbers(String numbers);

    List<CombinationTwo> findAllByNumbersIn(List<String> numbers);

}