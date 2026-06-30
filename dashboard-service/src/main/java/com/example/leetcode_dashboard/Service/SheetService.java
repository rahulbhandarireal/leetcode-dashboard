package com.example.leetcode_dashboard.Service;


import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import com.example.leetcode_dashboard.dto.SheetData;
import com.example.leetcode_dashboard.dto.SheetTransferDTO;
import com.example.leetcode_dashboard.model.LeetCodeProblem;
import com.example.leetcode_dashboard.model.Sheet;
import com.example.leetcode_dashboard.model.SheetProblem;
import com.example.leetcode_dashboard.repository.SheetProblemRepository;
import com.example.leetcode_dashboard.repository.SheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class SheetService {

    @Autowired
    private SheetRepository sheetRepository;
    @Autowired
    private SheetProblemRepository sheetProblemRepository;

    public List<SheetTransferDTO> getallsheetspresent() {
        List<SheetTransferDTO> sheetData=new ArrayList<>();
        List<Sheet> sheets=sheetRepository.findAll();
        sheets.forEach(sheet->{
            SheetTransferDTO sheetData1=SheetTransferDTO.builder()
                    .sheetId(sheet.getSheetId())
                    .name(sheet.getName())
                    .description(sheet.getDescription())
                    .total(sheet.getTotal())
                    .hard(sheet.getHard())
                    .type(sheet.getType())
                    .easy(sheet.getEasy())
                    .medium(sheet.getMedium())
                    .build();
            sheetData.add(sheetData1);
        });
        return sheetData;
    }

    public List<QuestionTransferDTO> getSheetByid(long sheetId) {
        Sheet sheet = sheetRepository
                .findBySheetId(sheetId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Sheet not found"));

        List<SheetProblem> relations =
                sheetProblemRepository
                        .findBySheet(sheet);

        return relations.stream()
                .map(relation -> {

                    LeetCodeProblem p =
                            relation.getProblem();

                    return  QuestionTransferDTO.builder()
                            .hints(p.getHints())
                            .title(p.getTitle())
                            .acceptanceRate(p.getAcceptanceRate())
                            .problemId(p.getProblemId())
                            .topicTags(p.getTopicTags())
                            .titleSlug(p.getTitleSlug())
                            .totalSubmissionRaw(p.getTotalSubmissionRaw())
                            .difficulty(p.getDifficulty())
                            .build();
                })
                .toList();


    }
}
