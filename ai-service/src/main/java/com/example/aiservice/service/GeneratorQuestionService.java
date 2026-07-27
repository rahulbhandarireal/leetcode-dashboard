package com.example.aiservice.service;

import com.example.aiservice.dto.AIProblemDTO;
import com.example.aiservice.model.ProblemEntity;

public interface GeneratorQuestionService {

    AIProblemDTO generateQuestion(String topic, String level);

    AIProblemDTO getbyid(long id);
}
