package com.example.aiservice.controller;


import com.example.aiservice.dto.AIProblemDTO;
import com.example.aiservice.model.ProblemEntity;
import com.example.aiservice.response.ApiResponse;
import com.example.aiservice.service.GeneratorQuestionService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/get")
public class AIController {


    @Autowired
    private GeneratorQuestionService generatorService;

    public AIController() {

    }

    @GetMapping("/problem")
    ApiResponse<AIProblemDTO> generation(@RequestParam String topic,@RequestParam String level) {
        AIProblemDTO problemEntity= generatorService.generateQuestion(topic,level);
        ApiResponse<AIProblemDTO> apiResponse = new ApiResponse<AIProblemDTO>();
        if (problemEntity != null) {
            apiResponse.setMessage("success");
            apiResponse.setSuccess(true);
            apiResponse.setData(problemEntity);
        }else{
            apiResponse.setMessage("fail");
            apiResponse.setSuccess(false);
            apiResponse.setData(null);
        }
    return apiResponse;
    }
    @GetMapping("/problem/{id}")
    public ApiResponse<AIProblemDTO> getById(@PathVariable("id") Long id) {
        AIProblemDTO problemEntity = generatorService.getbyid(id);
        ApiResponse<AIProblemDTO> apiResponse = new ApiResponse<>();

        if (problemEntity != null) {
            apiResponse.setMessage("success");
            apiResponse.setSuccess(true);
            apiResponse.setData(problemEntity);
        } else {
            apiResponse.setMessage("fail");
            apiResponse.setSuccess(false);
            apiResponse.setData(null);
        }

        return apiResponse;
    }
}
