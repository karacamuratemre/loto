package com.example.superloto.repository;

import com.example.superloto.entity.CombinationFive;
import com.example.superloto.entity.CombinationFour;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CombinationFourRepository extends MongoRepository<CombinationFour, String> {
    CombinationFour findByNumbers(String numbers);

    List<CombinationFour> findAllByNumbersIn(List<String> numbers);
}
