package com.example.superloto.repository;

import com.example.superloto.entity.CombinationThree;
import com.example.superloto.entity.CombinationThreeS;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CombinationThreeRepositoryS extends MongoRepository<CombinationThreeS, String> {
    CombinationThreeS findByNumbers(String numbers);
    List<CombinationThreeS> findAllByNumbersIn(List<String> numbers);
}
