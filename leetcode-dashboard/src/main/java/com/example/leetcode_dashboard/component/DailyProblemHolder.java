package com.example.leetcode_dashboard.component;

import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import com.example.leetcode_dashboard.model.LeetCodeProblem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DailyProblemHolder {

    private volatile QuestionTransferDTO leetCodeProblem;

}
