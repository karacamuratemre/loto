package com.example.superloto.service;

import com.example.superloto.entity.CombinationSuperAll;
import com.example.superloto.repository.CombinationSuperAllRepository;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CombinationDeleteService {

    @Autowired
    private CombinationSuperAllRepository combinationSuperAllRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    public void removeCombinationsContainingGivenSets(List<List<Integer>> inputSets) {
        // Tüm kombinasyonları veritabanından al
        List<CombinationSuperAll> allCombinations = combinationSuperAllRepository.findAll();

        List<String> combinationsToRemove = new ArrayList<>();

        for (List<Integer> inputSet : inputSets) {
            Set<Integer> inputSetAsSet = new HashSet<>(inputSet);

            for (CombinationSuperAll combination : allCombinations) {
                List<Integer> dbCombination = convertJsonToList(combination.getNumbers());

                // Eğer veritabanındaki kombinasyon, input setlerden birinin alt kümesini içeriyorsa silinecek listeye ekle
                if (containsSubsets(dbCombination, inputSetAsSet)) {
                    combinationsToRemove.add(combination.getNumbers());
                }
            }
        }

        // Belirlenen kombinasyonları sil
        if (!combinationsToRemove.isEmpty()) {
            combinationSuperAllRepository.deleteByNumbersIn(combinationsToRemove);
        }
    }

    private boolean containsSubsets(List<Integer> dbCombination, Set<Integer> inputSet) {
        // 4'lü, 5'li ve 6'lı tüm alt kümelerin kombinasyonlarını kontrol et
        for (int i = 4; i <= 6; i++) {
            List<List<Integer>> subsets = generateCombinations(new ArrayList<>(inputSet), i);
            for (List<Integer> subset : subsets) {
                if (dbCombination.containsAll(subset)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<List<Integer>> generateCombinations(List<Integer> numbers, int k) {
        List<List<Integer>> combinations = new ArrayList<>();
        combine(combinations, new ArrayList<>(), numbers, k, 0);
        return combinations;
    }

    private void combine(List<List<Integer>> combinations, List<Integer> current, List<Integer> numbers, int k, int start) {
        if (current.size() == k) {
            combinations.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < numbers.size(); i++) {
            current.add(numbers.get(i));
            combine(combinations, current, numbers, k, i + 1);
            current.remove(current.size() - 1);
        }
    }

    private List<Integer> convertJsonToList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Integer>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to List", e);
        }
    }
}
