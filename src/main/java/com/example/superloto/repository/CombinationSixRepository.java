package com.example.superloto.repository;

import com.example.superloto.entity.CombinationOne;
import com.example.superloto.entity.CombinationSix;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CombinationSixRepository extends MongoRepository<CombinationSix, String> {
    CombinationSix findByNumbers(String numbers);
    List<CombinationSix> findAllByNumbersIn(List<String> numbers);
}
