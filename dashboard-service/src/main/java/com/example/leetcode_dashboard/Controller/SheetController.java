package com.example.leetcode_dashboard.Controller;


import com.example.leetcode_dashboard.Service.SheetService;
import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import com.example.leetcode_dashboard.dto.SheetData;
import com.example.leetcode_dashboard.dto.SheetTransferDTO;
import com.example.leetcode_dashboard.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sheets")
public class SheetController {

    @Autowired
    private SheetService sheetService;


    @GetMapping("/all")
    public ApiResponse<List<SheetTransferDTO>> getallshests(){
        List<SheetTransferDTO> sheetData=sheetService.getallsheetspresent();
        ApiResponse<List<SheetTransferDTO>> apiResponse=new ApiResponse<>();
        if(!sheetData.isEmpty()) {
            apiResponse.setData(sheetData);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("success");
        }else{
            apiResponse.setSuccess(false);
            apiResponse.setMessage("error");
        }
        return apiResponse;
    }

    @GetMapping("/{sheetId}")
    public ApiResponse<List<QuestionTransferDTO>> getsheet(@PathVariable String sheetId){
        long id=Long.parseLong(sheetId);
        List<QuestionTransferDTO> sheetTransferDTO=sheetService.getSheetByid(id);
        ApiResponse<List<QuestionTransferDTO>> apiResponse=new ApiResponse<>();
        if(!sheetTransferDTO.isEmpty()) {
            apiResponse.setData(sheetTransferDTO);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("success");
        }else {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("error");
        }
        return apiResponse;

    }






}
