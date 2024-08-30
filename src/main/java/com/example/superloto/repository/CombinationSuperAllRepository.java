package com.example.superloto.repository;

import com.example.superloto.entity.CombinationSix;
import com.example.superloto.entity.CombinationSuperAll;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CombinationSuperAllRepository extends MongoRepository<CombinationSuperAll, String> {
    CombinationSuperAll findByNumbers(String numbers);
    List<CombinationSuperAll> findAllByNumbersIn(List<String> numbers);
    List<CombinationSuperAll> findByNumbersIn(List<String> numbers);
    void deleteByNumbersIn(List<String> numbers);
}
