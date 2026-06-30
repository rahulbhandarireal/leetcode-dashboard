package com.example.leetcode_dashboard.configs;


import com.example.leetcode_dashboard.dto.ProblemData;
import com.example.leetcode_dashboard.dto.SheetData;
import com.example.leetcode_dashboard.model.LeetCodeProblem;
import com.example.leetcode_dashboard.model.Sheet;
import com.example.leetcode_dashboard.model.SheetProblem;
import com.example.leetcode_dashboard.repository.LeetCodeProblemRepository;
import com.example.leetcode_dashboard.repository.SheetProblemRepository;
import com.example.leetcode_dashboard.repository.SheetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.InputStream;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final SheetProblemRepository sheetProblemRepository;
    private  final LeetCodeProblemRepository leetCodeRepository;
    private final SheetRepository sheetRepository;

    @Bean
    CommandLineRunner seedData(){

        return args -> {

            ObjectMapper  mapper = new ObjectMapper();

            Resource[] resources = new PathMatchingResourcePatternResolver().
                    getResources("classpath:sheets/*.json");

            for(Resource resource : resources){
                InputStream inputStream = resource.getInputStream();
                SheetData  sheetData = mapper.readValue(inputStream, SheetData.class);

                // checking if exists
                Optional<Sheet> existingsheet = sheetRepository.findBySheetId(sheetData.getSheetId());
                if(existingsheet.isPresent()){
                    System.out.println("Sheet already exists"+existingsheet.get().getName());
                    continue;
                }
                //create sheet
                Sheet sheet = Sheet.builder()
                        .sheetId(sheetData.getSheetId())
                        .name(sheetData.getName())
                        .type(sheetData.getType())
                        .description(
                                sheetData.getDescription()
                        )
                        .build();
                Sheet savedSheet = sheetRepository.save(sheet);
                int easy=0,medium=0,hard=0;
                for(ProblemData p : sheetData.getProblems()){
                    LeetCodeProblem problem =
                            leetCodeRepository
                                    .findByProblemId(
                                            p.getProblemId());
                    if(problem==null){
                        LeetCodeProblem
                                newProblem =
                                LeetCodeProblem
                                        .builder()
                                        .problemId(
                                                p.getProblemId())
                                        .title(
                                                p.getTitle())
                                        .titleSlug(
                                                p.getTitleSlug())
                                        .difficulty(
                                                p.getDifficulty())
                                        .build();
                           problem=leetCodeRepository
                                        .save(newProblem);
                    }

                    SheetProblem relation =
                            SheetProblem.builder()
                                    .sheet(savedSheet)
                                    .problem(problem)
                                    .position(
                                            p.getPosition())
                                    .build();

                    sheetProblemRepository
                            .save(relation);

                    // COUNT DIFFICULTIES
                    switch (
                            p.getDifficulty()) {

                        case "EASY" -> easy++;

                        case "MEDIUM" -> medium++;

                        case "HARD" -> hard++;
                    }


                }

                // UPDATE COUNTS
                savedSheet.setEasy(easy);

                savedSheet.setMedium(medium);

                savedSheet.setHard(hard);

                savedSheet.setTotal(
                        easy + medium + hard);

                sheetRepository.save(savedSheet);

                System.out.println(
                        "Inserted sheet: "
                                + savedSheet.getName());

            }
            System.out.println("All Sheets processed successfully");
        };

    }

}
