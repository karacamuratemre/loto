package com.example.superloto.repository;

import com.example.superloto.entity.CombinationOne;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CombinationOneRepository extends MongoRepository<CombinationOne, String> {
    CombinationOne findByNumbers(String numbers);
    List<CombinationOne> findAllByNumbersIn(List<String> numbers);
}