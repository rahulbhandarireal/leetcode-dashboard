package com.example.LeetDeCode_Battle_Module.controller;

import com.example.LeetDeCode_Battle_Module.DTO.BattleRoomView;
import com.example.LeetDeCode_Battle_Module.DTO.RunCodeRequest;
import com.example.LeetDeCode_Battle_Module.DTO.TestCaseResult;
import com.example.LeetDeCode_Battle_Module.DTO.submitCodeDTO;
import com.example.LeetDeCode_Battle_Module.model.BattleRoom;
import com.example.LeetDeCode_Battle_Module.response.ApiResponse;
import com.example.LeetDeCode_Battle_Module.service.CodeExecutionService;
import com.example.LeetDeCode_Battle_Module.service.ProblemService;
import com.example.LeetDeCode_Battle_Module.service.RoomService;
import com.example.LeetDeCode_Battle_Module.service.UserPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/execute")
public class CodeExecutionController {

    @Autowired
    private CodeExecutionService codeExecutionService;



    @Autowired
    private RoomService roomService;


    @PostMapping("/run/sample")
    public List<TestCaseResult> runCode(@RequestBody RunCodeRequest request) {
        List<TestCaseResult> resultList= codeExecutionService.runAllTestCases(request);
        return  resultList;
    }
    @PostMapping("/run/submit")
    public submitCodeDTO<List<TestCaseResult>> submitCode(@RequestBody RunCodeRequest request) {
        List<TestCaseResult> resultList = codeExecutionService.runAllTestCases(request);
        int totalScore = 1000 - request.getNegativeScore() * 10;
        boolean allTestPassed = true;
        submitCodeDTO<List<TestCaseResult>> resultDTO = new submitCodeDTO<>();
        resultDTO.setData(resultList);
        for(int i=0;i<resultList.size();i++){
            if(!resultList.get(i).isPassed()){
                allTestPassed = false;
                resultDTO.setFailedCase(i);
                resultDTO.setErrorMessage(resultList.get(i).getError());
                resultDTO.setAllPassed(false);
                break;
            }
        }


        if(allTestPassed) {
            resultDTO.setAllPassed(true);
            BattleRoomView battleRoomView = roomService.
                    submitPlayerCode(request.getRoomCode(), request.getPlayerId(), request.getLanguage(), request.getCode(), totalScore,allTestPassed);
            if (battleRoomView.getWinnerPlayerId() != null  && battleRoomView.getWinnerPlayerId().equals(request.getPlayerId())) {
                resultDTO.setWinner(true);

            } else {
                resultDTO.setWinner(false);

            }

        }
        return  resultDTO;
    }

    @PostMapping("/run/forcefullsubmit")
    public ApiResponse<Integer> forcefullSubmitCode(@RequestBody RunCodeRequest request) {
        BattleRoomView battleRoomView = roomService.
                submitPlayerCode(request.getRoomCode(), request.getPlayerId(), request.getLanguage(), request.getCode(), -10,false);
        ApiResponse<Integer> response = new ApiResponse<>();
        response.setStatus(true);
        response.setMessage("You lost by forcefull submit");
        response.setData(-10);
        return response;
    }
}