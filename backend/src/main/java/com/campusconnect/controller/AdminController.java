package com.campusconnect.controller;

import com.campusconnect.entity.Answer;
import com.campusconnect.entity.Event;
import com.campusconnect.entity.Question;
import com.campusconnect.entity.User;
import com.campusconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final AnnouncementRepository announcementRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Deleting a user can fail with a foreign key constraint violation if that user
     * has created events/announcements/forum posts, or joined events as a participant.
     * This cleans up all of that first, then removes the account itself.
     */
    @DeleteMapping("/users/{id}")
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // 1. Remove this user from any events they joined as a participant (not creator)
        List<Event> joinedEvents = eventRepository.findByParticipants_Id(id);
        for (Event event : joinedEvents) {
            event.getParticipants().removeIf(u -> u.getId().equals(id));
            eventRepository.save(event);
        }

        // 2. Delete answers this user wrote (on other people's questions)
        List<Answer> answers = answerRepository.findByAnsweredBy_Id(id);
        answerRepository.deleteAll(answers);

        // 3. Delete questions this user asked (cascades to their own answers automatically)
        List<Question> questions = questionRepository.findByAskedBy_Id(id);
        questionRepository.deleteAll(questions);

        // 4. Delete announcements this user posted
        announcementRepository.deleteAll(announcementRepository.findByPostedBy_Id(id));

        // 5. Delete events this user created (also clears that event's participant rows)
        eventRepository.deleteAll(eventRepository.findByCreatedBy_Id(id));

        // 6. Finally, delete the user account itself
        userRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        if (!eventRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        eventRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
