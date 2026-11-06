package com.campusconnect.repository;

import com.campusconnect.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByEvent_IdAndUser_Id(Long eventId, Long userId);
    long countByEvent_Id(Long eventId);
    Optional<EventRegistration> findByEvent_IdAndUser_Id(Long eventId, Long userId);
    List<EventRegistration> findAllByEvent_IdOrderByRegisteredAtAsc(Long eventId);
}
