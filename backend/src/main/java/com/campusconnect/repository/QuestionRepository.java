package com.campusconnect.repository;

import com.campusconnect.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByAskedBy_Id(Long userId);
}
