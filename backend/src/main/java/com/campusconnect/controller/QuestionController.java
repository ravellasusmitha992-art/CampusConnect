package com.campusconnect.controller;

import com.campusconnect.dto.AnswerRequest;
import com.campusconnect.dto.AnswerResponse;
import com.campusconnect.dto.QuestionRequest;
import com.campusconnect.dto.QuestionResponse;
import com.campusconnect.entity.Answer;
import com.campusconnect.entity.Question;
import com.campusconnect.entity.Role;
import com.campusconnect.entity.User;
import com.campusconnect.repository.AnswerRepository;
import com.campusconnect.repository.QuestionRepository;
import com.campusconnect.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @GetMapping
    public List<QuestionResponse> getAll() {
        return questionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> create(
            @Valid @RequestBody QuestionRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        User user = principal.getUser();
        Question question = Question.builder()
                .title(request.getTitle().trim())
                .content(request.getContent().trim())
                .askedBy(user)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(questionRepository.save(question)));
    }

    @PostMapping("/{id}/answers")
    public ResponseEntity<AnswerResponse> answer(
            @PathVariable Long id,
            @Valid @RequestBody AnswerRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

        Answer answer = Answer.builder()
                .content(request.getContent().trim())
                .answeredBy(principal.getUser())
                .question(question)
                .build();

        Answer saved = answerRepository.save(answer);
        return ResponseEntity.status(HttpStatus.CREATED).body(toAnswerResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

        User currentUser = principal.getUser();
        boolean isOwner = question.getAskedBy() != null
                && question.getAskedBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only delete your own questions");
        }

        questionRepository.delete(question);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable Long questionId,
            @PathVariable Long answerId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found"));

        if (answer.getQuestion() == null || !answer.getQuestion().getId().equals(question.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found for this question");
        }

        User currentUser = principal.getUser();
        boolean isOwner = answer.getAnsweredBy() != null
                && answer.getAnsweredBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only delete your own answers");
        }

        answerRepository.delete(answer);
        return ResponseEntity.noContent().build();
    }

    private QuestionResponse toResponse(Question question) {
        List<AnswerResponse> answers = question.getAnswers() == null
                ? List.of()
                : question.getAnswers().stream().map(this::toAnswerResponse).toList();

        return new QuestionResponse(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                question.getAskedBy() == null ? null : question.getAskedBy().getId(),
                question.getAskedBy() == null ? "Unknown user" : question.getAskedBy().getFullName(),
                question.getCreatedAt(),
                answers
        );
    }

    private AnswerResponse toAnswerResponse(Answer answer) {
        return new AnswerResponse(
                answer.getId(),
                answer.getContent(),
                answer.getAnsweredBy() == null ? null : answer.getAnsweredBy().getId(),
                answer.getAnsweredBy() == null ? "Unknown user" : answer.getAnsweredBy().getFullName(),
                answer.getCreatedAt()
        );
    }
}
