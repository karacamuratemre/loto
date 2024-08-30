package com.example.superloto.repository;

import com.example.superloto.entity.CombinationOne;
import com.example.superloto.entity.CombinationOneS;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CombinationOneRepositoryS extends MongoRepository<CombinationOneS, String> {
    CombinationOneS findByNumbers(String numbers);
    List<CombinationOneS> findAllByNumbersIn(List<String> numbers);
}