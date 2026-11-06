package com.campusconnect.controller;

import com.campusconnect.dto.AnswerRequest;
import com.campusconnect.dto.QuestionRequest;
import com.campusconnect.entity.Answer;
import com.campusconnect.entity.Question;
import com.campusconnect.entity.User;
import com.campusconnect.repository.AnswerRepository;
import com.campusconnect.repository.QuestionRepository;
import com.campusconnect.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @GetMapping
    public List<Question> getAll() {
        return questionRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Question> create(@Valid @RequestBody QuestionRequest request,
                                            @AuthenticationPrincipal CustomUserDetails principal) {
        User user = principal.getUser();
        Question question = Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .askedBy(user)
                .build();
        return ResponseEntity.ok(questionRepository.save(question));
    }

    @PostMapping("/{id}/answers")
    public ResponseEntity<Answer> answer(@PathVariable Long id,
                                          @Valid @RequestBody AnswerRequest request,
                                          @AuthenticationPrincipal CustomUserDetails principal) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Answer answer = Answer.builder()
                .content(request.getContent())
                .answeredBy(principal.getUser())
                .question(question)
                .build();

        return ResponseEntity.ok(answerRepository.save(answer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        questionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
