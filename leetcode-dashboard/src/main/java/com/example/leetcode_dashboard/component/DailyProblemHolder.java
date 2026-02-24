package com.example.leetcode_dashboard.component;


import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class DailyProblemHolder {

    private volatile QuestionTransferDTO currentProblem;

}
