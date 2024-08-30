package com.example.superloto.repository;

import com.example.superloto.entity.CombinationFour;
import com.example.superloto.entity.CombinationFourS;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CombinationFourRepositoryS extends MongoRepository<CombinationFourS, String> {
    CombinationFourS findByNumbers(String numbers);

    List<CombinationFourS> findAllByNumbersIn(List<String> numbers);
}
