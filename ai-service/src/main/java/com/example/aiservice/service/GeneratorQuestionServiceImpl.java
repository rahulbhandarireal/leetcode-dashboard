package com.example.aiservice.service;

import com.example.aiservice.dto.AIProblemDTO;
import com.example.aiservice.model.AiGeneratedProblem;
import com.example.aiservice.model.AiTestCase;
import com.example.aiservice.model.ProblemEntity;
import com.example.aiservice.repository.AiGeneratedProblemRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

import static com.example.aiservice.dto.AIProblemDTO.convertToAIDto;

@Service
public class GeneratorQuestionServiceImpl implements GeneratorQuestionService {

    private final ChatClient openAiChatClient;

    @Value("classpath:/prompts/system-prompt.st")
    private Resource systemprompt;

    @Value("classpath:/prompts/user-prompt.st")
    private Resource userprompt;

    @Value("${platform.problems.max-generation-limit}")
    private int maxLimit;

    @Autowired
    private AiGeneratedProblemRepository aiGeneratedProblemRepository;

    public GeneratorQuestionServiceImpl(@Qualifier("openAiChatClient") ChatClient openAiChatClient) {
        this.openAiChatClient = openAiChatClient;
    }



    @Override
    public AIProblemDTO generateQuestion(String topic, String level) {
        AIProblemDTO problemDTO = null;
        int count = aiGeneratedProblemRepository.getProblemCount(topic, level);

        if (count >= maxLimit) {
            // 1. LIMIT BREACHED: Select a random question from the database
            AiGeneratedProblem existingProblem = aiGeneratedProblemRepository
                    .findRandomByTopicAndLevel(topic, level)
                    .orElseThrow(() -> new RuntimeException("Problem count logic indicated records exist, but none were retrieved."));

            // Map the database entity layout cleanly back to your DTO
            problemDTO = convertToAIDto(existingProblem);

        } else {
            // 2. GENERATE NEW PROBLEM: Call OpenAI using Structured Entity Mapping
            ProblemEntity problemEntity = openAiChatClient.prompt()
                    .system(systemprompt)
                    .user(u -> u.text(userprompt)
                            .param("topic", topic)
                            .param("level", level))
                    .call()
                    .entity(ProblemEntity.class);

            List<AiTestCase> sampleaitest = new ArrayList<>();
            List<AiTestCase> aitest = new ArrayList<>();

            problemEntity.sampleTestCases().forEach(it -> {
                sampleaitest.add(AiTestCase.builder()
                        .input(it.input())
                        .output(it.output())
                        .explanation(it.explanation())
                        .build());
            });

            problemEntity.testcase().forEach(it -> {
                aitest.add(AiTestCase.builder()
                        .input(it.input())
                        .output(it.output())
                        .explanation(it.explanation())
                        .build());
            });

            // Map fully structured metadata to the presentation DTO layer
            problemDTO = AIProblemDTO.builder()
                    .title(problemEntity.title())
                    .topic(topic)
                    .level(level)
                    .problemStatement(problemEntity.problemStatement())
                    .constraint(problemEntity.constraint())
                    .hints(problemEntity.hints())
                    .sampleTestCases(sampleaitest)
                    .testcase(aitest)
                    .build();

            // 3. ATOMIC TRANSACTION SAVE
            // Build the database entity wrapper
            AiGeneratedProblem aiGeneratedProblem = AiGeneratedProblem.builder()
                    .title(problemDTO.getTitle())
                    .topic(problemDTO.getTopic())
                    .level(problemDTO.getLevel())
                    .problemStatement(problemDTO.getProblemStatement())
                    .constraint(problemDTO.getConstraint())
                    .hints(problemDTO.getHints())
                    .sampleTestCases(problemDTO.getSampleTestCases())
                    .testcase(problemDTO.getTestcase())
                    .build();

            // Because of CascadeType.ALL, saving the parent saves all linked AiTestCase entities in one execution!
            aiGeneratedProblemRepository.save(aiGeneratedProblem);
        }

        return problemDTO;
    }

    @Override
    public AIProblemDTO getbyid(long id) {
       AiGeneratedProblem aiGeneratedProblem=aiGeneratedProblemRepository.getById(id);
       return convertToAIDto(aiGeneratedProblem);
    }
}