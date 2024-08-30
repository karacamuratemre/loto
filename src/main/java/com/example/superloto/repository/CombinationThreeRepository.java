package com.example.superloto.repository;

import com.example.superloto.entity.CombinationSix;
import com.example.superloto.entity.CombinationThree;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CombinationThreeRepository extends MongoRepository<CombinationThree, String> {
    CombinationThree findByNumbers(String numbers);
    List<CombinationThree> findAllByNumbersIn(List<String> numbers);
}
