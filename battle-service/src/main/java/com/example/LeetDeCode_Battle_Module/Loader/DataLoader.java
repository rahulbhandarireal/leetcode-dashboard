package com.example.LeetDeCode_Battle_Module.Loader;

import com.example.LeetDeCode_Battle_Module.model.Question;
import com.example.LeetDeCode_Battle_Module.repository.QuestionRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;


@AllArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {

    private final QuestionRepository repository;
    private final ObjectMapper objectMapper;


    @Override
    public void run(String... args) throws Exception {

        if (repository.count() > 0) {
            return; // prevent duplicate insert
        }

        InputStream input = getClass()
                .getResourceAsStream("/questions.json");

        List<Question> questions = Arrays.asList(
                objectMapper.readValue(input, Question[].class)
        );

        // Initialize usedCount
        questions.forEach(q -> q.setUsedCount(0));

        repository.saveAll(questions);

        System.out.println("✅ Questions loaded: " + questions.size());

    }
}
