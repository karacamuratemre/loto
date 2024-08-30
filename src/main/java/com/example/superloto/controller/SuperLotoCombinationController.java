package com.example.superloto.controller;

import com.example.superloto.service.CombinationDeleteService;
import com.example.superloto.service.SuperLotoCombinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/superloto")
public class SuperLotoCombinationController {
    @Autowired
    private SuperLotoCombinationService combinationService;
    @Autowired
    private CombinationDeleteService combinationDeleteService;
    @PostMapping("/generate")
    public String generateCombinations(@RequestBody List<List<Integer>> drawnNumbers) {
        if (drawnNumbers.isEmpty()) {
            return "You must provide at least one set of numbers.";
        }

        try {
            for (List<Integer> numbers : drawnNumbers) {
                if (numbers.size() != 6) {
                    return "Each set must contain exactly 6 numbers.";
                }
                combinationService.generateAndSaveCombinations(numbers);
            }
        } catch (Exception e) {
            return "An error occurred: " + e.getMessage();
        }

        return "Combinations generated and saved to the database.";
    }

    @PostMapping("/insertAllSixCombinations")
    public String generateAllSixNumberCombinations() {
        try {
            combinationService.generateAndSaveAllSixNumberCombinations();
        } catch (Exception e) {
            return "An error occurred: " + e.getMessage();
        }

        return "All 6-number combinations for numbers 1-60 have been generated and saved to the database.";
    }

    @PostMapping("/removeCombinations")
    public String removeCombinations(@RequestBody List<List<Integer>> inputSets) {
        if (inputSets.isEmpty()) {
            return "Input set list cannot be empty.";
        }

        try {
            combinationDeleteService.removeCombinationsContainingGivenSets(inputSets);
        } catch (Exception e) {
            return "An error occurred: " + e.getMessage();
        }

        return "Combinations containing given sets have been removed from the database.";
    }
}
