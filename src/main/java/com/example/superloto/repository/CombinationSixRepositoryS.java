package com.example.superloto.repository;

import com.example.superloto.entity.CombinationSix;
import com.example.superloto.entity.CombinationSixS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CombinationSixRepositoryS extends MongoRepository<CombinationSixS, String> {
    CombinationSixS findByNumbers(String numbers);
    List<CombinationSixS> findAllByNumbersIn(List<String> numbers);
}
