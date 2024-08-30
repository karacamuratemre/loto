package com.example.superloto.controller;

import com.example.superloto.service.SayisalLotoCombinationService;
import com.example.superloto.service.SuperLotoCombinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/sayisalloto")
public class SayisalLotoCombinationController {
    @Autowired
    private SayisalLotoCombinationService combinationService;

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
}
