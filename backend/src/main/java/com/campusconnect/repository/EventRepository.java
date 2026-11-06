package com.campusconnect.repository;

import com.campusconnect.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCreatedBy_Id(Long userId);
    List<Event> findByParticipants_Id(Long userId);
}
