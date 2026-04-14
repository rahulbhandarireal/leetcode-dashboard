package com.example.LeetDeCode_Battle_Module.repository;

import com.example.LeetDeCode_Battle_Module.model.Question;
import com.example.LeetDeCode_Battle_Module.model.Topic;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM Question q WHERE q.topic =:topic ORDER BY q.usedCount ASC")
    List<Question> findLeastUsedQuestion(Topic topic, Pageable pageable);
}
