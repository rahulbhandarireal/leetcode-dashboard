package com.example.LeetDeCode_Battle_Module.controller;


import com.example.LeetDeCode_Battle_Module.model.Userpoints;
import com.example.LeetDeCode_Battle_Module.repository.UserPointsRepository;
import com.example.LeetDeCode_Battle_Module.response.ApiResponse;
import com.example.LeetDeCode_Battle_Module.service.UserPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserPointsController {

    @Autowired
    private UserPointService userPointService;

    @GetMapping("/getuserpoints")
    private ApiResponse<Userpoints> getUserPoints(@RequestParam String playerId){
        Userpoints u= userPointService.getUserpoint(playerId);
        ApiResponse<Userpoints> res=new ApiResponse<>();
        res.setData(u);
        res.setMessage("success");
        res.setStatus(true);
        return res;
    }
}
