package com.example.superloto.service;
import com.example.superloto.entity.*;
import com.example.superloto.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
@Service
public class SuperLotoCombinationService {

    @Autowired
    private CombinationOneRepository combinationOneRepository;
    @Autowired
    private CombinationTwoRepository combinationTwoRepository;
    @Autowired
    private CombinationThreeRepository combinationThreeRepository;
    @Autowired
    private CombinationFourRepository combinationFourRepository;
    @Autowired
    private CombinationFiveRepository combinationFiveRepository;
    @Autowired
    private CombinationSixRepository combinationSixRepository;
    @Autowired
    private CombinationSuperAllRepository combinationSuperAllRepository;

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

    @Transactional
    public void generateAndSaveAllSixNumberCombinations() {
        List<List<Integer>> combinations = generateSixNumberCombinations(60, 6);

        List<CombinationSuperAll> batchToSave = new ArrayList<>();

        for (List<Integer> combination : combinations) {
            String combinationJson = convertToJson(combination);
            CombinationSuperAll newCombination = new CombinationSuperAll(combinationJson, 1);
            batchToSave.add(newCombination);

            if (batchToSave.size() >= BATCH_SIZE) {
                combinationSuperAllRepository.saveAll(batchToSave); // Toplu olarak kaydet
                batchToSave.clear();
            }
        }

        if (!batchToSave.isEmpty()) {
            combinationSuperAllRepository.saveAll(batchToSave); // Kalanları kaydet
        }
    }

    public List<List<Integer>> generateSixNumberCombinations(int n, int k) {
        List<List<Integer>> combinations = new ArrayList<>();
        combineSuper(combinations, new ArrayList<>(), n, k, 1);
        return combinations;
    }

    private void combineSuper(List<List<Integer>> combinations, List<Integer> current, int n, int k, int start) {
        if (current.size() == k) {
            combinations.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i <= n; i++) {
            current.add(i);  // Kombinasyona sayıyı ekle
            combineSuper(combinations, current, n, k, i + 1);  // Sonraki kombinasyonlar için rekürsif çağrı
            current.remove(current.size() - 1);  // Son eklenen sayıyı çıkar ve diğer olasılıkları dene
        }
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
        if (repository instanceof CombinationOneRepository) {
            return (T) new CombinationOne(combinationJson, count);
        } else if (repository instanceof CombinationTwoRepository) {
            return (T) new CombinationTwo(combinationJson, count);
        } else if (repository instanceof CombinationThreeRepository) {
            return (T) new CombinationThree(combinationJson, count);
        } else if (repository instanceof CombinationFourRepository) {
            return (T) new CombinationFour(combinationJson, count);
        } else if (repository instanceof CombinationFiveRepository) {
            return (T) new CombinationFive(combinationJson, count);
        } else {
            return (T) new CombinationSix(combinationJson, count);
        }
    }

    private <T> Map<String, T> findExistingCombinations(MongoRepository<T, String> repository, List<String> combinationJsons) {
        if (repository instanceof CombinationOneRepository) {
            List<CombinationOne> existingCombinations = ((CombinationOneRepository) repository).findAllByNumbersIn(combinationJsons);
            return existingCombinations.stream()
                    .collect(Collectors.toMap(CombinationOne::getNumbers, combination -> (T) combination));
        } else if (repository instanceof CombinationTwoRepository) {
            List<CombinationTwo> existingCombinations = ((CombinationTwoRepository) repository).findAllByNumbersIn(combinationJsons);
            return existingCombinations.stream()
                    .collect(Collectors.toMap(CombinationTwo::getNumbers, combination -> (T) combination));
        } else if (repository instanceof CombinationThreeRepository) {
            List<CombinationThree> existingCombinations = ((CombinationThreeRepository) repository).findAllByNumbersIn(combinationJsons);
            return existingCombinations.stream()
                    .collect(Collectors.toMap(CombinationThree::getNumbers, combination -> (T) combination));
        } else if (repository instanceof CombinationFourRepository) {
            List<CombinationFour> existingCombinations = ((CombinationFourRepository) repository).findAllByNumbersIn(combinationJsons);
            return existingCombinations.stream()
                    .collect(Collectors.toMap(CombinationFour::getNumbers, combination -> (T) combination));
        } else if (repository instanceof CombinationFiveRepository) {
            List<CombinationFive> existingCombinations = ((CombinationFiveRepository) repository).findAllByNumbersIn(combinationJsons);
            return existingCombinations.stream()
                    .collect(Collectors.toMap(CombinationFive::getNumbers, combination -> (T) combination));
        } else if (repository instanceof CombinationSixRepository) {
            List<CombinationSix> existingCombinations = ((CombinationSixRepository) repository).findAllByNumbersIn(combinationJsons);
            return existingCombinations.stream()
                    .collect(Collectors.toMap(CombinationSix::getNumbers, combination -> (T) combination));
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
