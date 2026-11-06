package com.campusconnect.repository;

import com.campusconnect.entity.Question;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByAskedBy_Id(Long userId);

    @EntityGraph(attributePaths = {"askedBy", "answers", "answers.answeredBy"})
    List<Question> findAllByOrderByCreatedAtDesc();
}
