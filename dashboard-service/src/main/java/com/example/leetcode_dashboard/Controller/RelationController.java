package com.example.leetcode_dashboard.Controller;


import com.example.leetcode_dashboard.Service.RelationService;
import com.example.leetcode_dashboard.dto.UserStatsResponse;
import com.example.leetcode_dashboard.model.Student;
import com.example.leetcode_dashboard.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/relation")
public class RelationController {

    @Autowired
    private RelationService relationService;


    @PostMapping("/makerelation/{user}/{friend}")
    public ApiResponse<UserStatsResponse> addasknown(@PathVariable String user, @PathVariable  String friend){

        ApiResponse<UserStatsResponse> response = new ApiResponse<>();
        try {
            UserStatsResponse isdone = relationService.makeasfriend(user, friend);
            response.setMessage("success");
            response.setData(isdone);
            response.setSuccess(true);
        }catch (Exception e){
            response.setMessage("fail");
            response.setSuccess(false);
        }
        return response;
    }

    @GetMapping("/findallknown/{username}")
    ApiResponse<List<UserStatsResponse>> findAllKnown(@PathVariable String username){
        ApiResponse<List<UserStatsResponse>> response = new ApiResponse<>();
        try {
            List<UserStatsResponse> knows = relationService.findallKnown(username);
            response.setData(knows);
            response.setMessage("success");
            response.setSuccess(true);
        }catch (Exception e){
            response.setMessage("error");
            response.setSuccess(false);
            e.printStackTrace();
        }
        return response;
    }

    @DeleteMapping("/deleteknown/{user}/{known}")
    public ApiResponse<String> deleteKnown(@PathVariable String user, @PathVariable String known) {
        boolean isdone = relationService.deleteKnown(user, known);
        ApiResponse<String> response = new ApiResponse<>();
        response.setSuccess(isdone);

        if (isdone) {
            response.setMessage("success");
            response.setData("Deleted known relation");
        } else {
            response.setMessage("No relation found");
            response.setData("No known relation exists between " + user + " and " + known);
        }
        return response;
    }

}
