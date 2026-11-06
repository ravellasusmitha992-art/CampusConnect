package com.campusconnect.repository;

import com.campusconnect.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByAnsweredBy_Id(Long userId);
}
