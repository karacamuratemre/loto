package com.example.superloto.service;

import com.example.superloto.entity.*;
import com.example.superloto.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class SayisalLotoCombinationService {
    @Autowired
    private CombinationOneRepositoryS combinationOneRepository;
    @Autowired
    private CombinationTwoRepositoryS combinationTwoRepository;
    @Autowired
    private CombinationThreeRepositoryS combinationThreeRepository;
    @Autowired
    private CombinationFourRepositoryS combinationFourRepository;
    @Autowired
    private CombinationFiveRepositoryS combinationFiveRepository;
    @Autowired
    private CombinationSixRepositoryS combinationSixRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final int BATCH_SIZE = 10000; // Batch size for batch operations

    @Transactional
    public void generateAndSaveCombinations(List<Integer> drawnNumbers) throws JsonProcessingException, InterruptedException, ExecutionException {
        List<Callable<Void>> tasks = new ArrayList<>();

        // Create tasks for parallel processing
        tasks.add(() -> {
            processCombinations(drawnNumbers, 1, combinationOneRepository);
            return null;
        });
        tasks.add(() -> {
            processCombinations(drawnNumbers, 2, combinationTwoRepository);
            return null;
        });
        tasks.add(() -> {
            processCombinations(drawnNumbers, 3, combinationThreeRepository);
            return null;
        });
        tasks.add(() -> {
            processCombinations(drawnNumbers, 4, combinationFourRepository);
            return null;
        });
        tasks.add(() -> {
            processCombinations(drawnNumbers, 5, combinationFiveRepository);
            return null;
        });
        tasks.add(() -> {
            processCombinations(drawnNumbers, 6, combinationSixRepository);
            return null;
        });

        // Use ExecutorService for parallel processing
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Void>> futures = executorService.invokeAll(tasks);

        for (Future<Void> future : futures) {
            future.get(); // Wait for all tasks to complete
        }

        executorService.shutdown();
    }
    private <T> void processCombinations(List<Integer> drawnNumbers, int setSize, MongoRepository<T, String> repository) throws JsonProcessingException {
        List<List<Integer>> combinations = generateCombinations(drawnNumbers, setSize);
        Map<String, Long> combinationCountMap = combinations.stream()
                .collect(Collectors.groupingBy(
                        this::convertToJson,
                        Collectors.counting()
                ));

        // Retrieve existing combinations in one query
        List<String> jsonCombinations = new ArrayList<>(combinationCountMap.keySet());
        Map<String, T> existingCombinationsMap = findExistingCombinations(repository, jsonCombinations);

        List<T> batchToSave = new ArrayList<>();

        for (Map.Entry<String, Long> entry : combinationCountMap.entrySet()) {
            String combinationJson = entry.getKey();
            int count = entry.getValue().intValue();

            T existingCombination = existingCombinationsMap.get(combinationJson);
            if (existingCombination != null) {
                updateCombinationCount(existingCombination, count);
                batchToSave.add(existingCombination);
            } else {
                T newCombination = createNewCombination(repository, combinationJson, count);
                batchToSave.add(newCombination);
            }

            if (batchToSave.size() >= BATCH_SIZE) {
                repository.saveAll(batchToSave); // Save in batch
                batchToSave.clear();
            }
        }

        if (!batchToSave.isEmpty()) {
            repository.saveAll(batchToSave); // Save remaining
        }
    }

    private String convertToJson(List<Integer> combination) {
        try {
            return objectMapper.writeValueAsString(combination);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
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
            current.add(numbers.get(i));  // Kombinasyona sayıyı ekle
            combine(combinations, current, numbers, k, i + 1);  // Sonraki kombinasyonlar için recursive çağrı
            current.remove(current.size() - 1);  // Son eklenen sayıyı çıkar ve diğer olasılıkları dene
        }
    }
    private <T> T createNewCombination(MongoRepository<T, String> repository, String combinationJson, int count) {
        if (repository instanceof CombinationOneRepositoryS) {
            return (T) new CombinationOneS(combinationJson, count);
        } else if (repository instanceof CombinationTwoRepositoryS) {
            return (T) new CombinationTwoS(combinationJson, count);
        } else if (repository instanceof CombinationThreeRepositoryS) {
            return (T) new CombinationThreeS(combinationJson, count);
        } else if (repository instanceof CombinationFourRepositoryS) {
            return (T) new CombinationFourS(combinationJson, count);
        } else if (repository instanceof CombinationFiveRepositoryS) {
            return (T) new CombinationFiveS(combinationJson, count);
        } else {
            return (T) new CombinationSixS(combinationJson, count);
        }
    }
    private <T> Map<String, T> findExistingCombinations(MongoRepository<T, String> repository, List<String> combinationJsons) {
        if (repository instanceof CombinationOneRepositoryS) {
            List<CombinationOneS> existingCombinations = ((CombinationOneRepositoryS) repository).findAllByNumbersIn(combinationJsons);
            return existingCombinations.stream()
                    .collect(Collectors.toMap(CombinationOneS::getNumbers, combination -> (T) combination));
        } else if (repository instanceof CombinationTwoRepositoryS) {
            List<CombinationTwoS> existingCombinations = ((CombinationTwoRepositoryS) repository).findAllByNumbersIn(combinationJsons);
            return existingCombinations.stream()
                    .collect(Collectors.toMap(CombinationTwoS::getNumbers, combination -> (T) combination));
        } else if (repository instanceof CombinationThreeRepositoryS) {
            List<CombinationThreeS> existingCombinations = ((CombinationThreeRepositoryS) repository).findAllByNumbersIn(combinationJsons);
            return existingCombinations.stream()
                    .collect(Collectors.toMap(CombinationThreeS::getNumbers, combination -> (T) combination));
        } else if (repository instanceof CombinationFourRepositoryS) {
            List<CombinationFourS> existingCombinations = ((CombinationFourRepositoryS) repository).findAllByNumbersIn(combinationJsons);
            return existingCombinations.stream()
                    .collect(Collectors.toMap(CombinationFourS::getNumbers, combination -> (T) combination));
        } else if (repository instanceof CombinationFiveRepositoryS) {
            List<CombinationFiveS> existingCombinations = ((CombinationFiveRepositoryS) repository).findAllByNumbersIn(combinationJsons);
            return existingCombinations.stream()
                    .collect(Collectors.toMap(CombinationFiveS::getNumbers, combination -> (T) combination));
        } else if (repository instanceof CombinationSixRepositoryS) {
            List<CombinationSixS> existingCombinations = ((CombinationSixRepositoryS) repository).findAllByNumbersIn(combinationJsons);
            return existingCombinations.stream()
                    .collect(Collectors.toMap(CombinationSixS::getNumbers, combination -> (T) combination));
        }
        return new HashMap<>();
    }
    private <T> void updateCombinationCount(T combination, int additionalCount) {
        if (combination instanceof CombinationOne) {
            CombinationOne combinationOne = (CombinationOne) combination;
            combinationOne.setCount(combinationOne.getCount() + additionalCount);
        } else if (combination instanceof CombinationTwo) {
            CombinationTwo combinationTwo = (CombinationTwo) combination;
            combinationTwo.setCount(combinationTwo.getCount() + additionalCount);
        } else if (combination instanceof CombinationThree) {
            CombinationThree combinationThree = (CombinationThree) combination;
            combinationThree.setCount(combinationThree.getCount() + additionalCount);
        } else if (combination instanceof CombinationFour) {
            CombinationFour combinationFour = (CombinationFour) combination;
            combinationFour.setCount(combinationFour.getCount() + additionalCount);
        } else if (combination instanceof CombinationFive) {
            CombinationFive combinationFive = (CombinationFive) combination;
            combinationFive.setCount(combinationFive.getCount() + additionalCount);
        } else if (combination instanceof CombinationSix) {
            CombinationSix combinationSix = (CombinationSix) combination;
            combinationSix.setCount(combinationSix.getCount() + additionalCount);
        }
    }
}
