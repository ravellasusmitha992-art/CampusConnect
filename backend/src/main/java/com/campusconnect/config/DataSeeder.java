package com.campusconnect.config;

import com.campusconnect.entity.*;
import com.campusconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Seeds demo data (one faculty account, 6 events, 6 announcements) on first run
 * so the app has visible content immediately, instead of an empty dashboard.
 * Safe to run repeatedly - it only seeds if the events table is empty.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final AnnouncementRepository announcementRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (eventRepository.count() > 0) {
            return; // already seeded
        }

        User demoFaculty = userRepository.findByEmail("faculty.demo@campusconnect.edu")
                .orElseGet(() -> userRepository.save(User.builder()
                        .fullName("Narendra")
                        .email("faculty.demo@campusconnect.edu")
                        .password(passwordEncoder.encode("faculty123"))
                        .role(Role.FACULTY)
                        .department("Computer Science")
                        .enabled(true)
                        .build()));

        String[][] events = {
            {"Annual Tech Fest 2026 - CodeSprint", "24-hour hackathon. Teams of up to 4 build a prototype around this year's theme: Smart Campus Solutions. Prizes for top 3 teams plus food and swag for all.", "Main Auditorium, IT Block", "https://picsum.photos/seed/codesprint/600/300"},
            {"National Level Symposium on AI & Robotics", "Guest lectures from industry experts, paper presentation track, and a robotics demo zone. Open to all branches.", "Seminar Hall, Block C", "https://picsum.photos/seed/aisymposium/600/300"},
            {"Inter-College Cultural Fest - Rhythms 2026", "Dance, music, and drama competitions across three days. Cash prizes worth ₹1,00,000 for winners.", "Open Air Theatre", "https://picsum.photos/seed/rhythms/600/300"},
            {"Placement Drive - TCS & Infosys", "On-campus recruitment drive for final year students. Bring resume, ID card, and academic transcripts.", "Placement Cell, Admin Block", "https://picsum.photos/seed/placement/600/300"},
            {"Blood Donation Camp", "Organized with the local Red Cross chapter. All students and staff aged 18+ are welcome to donate.", "Health Centre Lawn", "https://picsum.photos/seed/blooddonation/600/300"},
            {"Alumni Meet 2026", "Reconnect with graduates from the past decade. Networking session followed by dinner.", "College Amphitheatre", "https://picsum.photos/seed/alumnimeet/600/300"},
        };

        for (int i = 0; i < events.length; i++) {
            eventRepository.save(Event.builder()
                    .title(events[i][0])
                    .description(events[i][1])
                    .venue(events[i][2])
                    .posterUrl(events[i][3])
                    .eventDate(LocalDateTime.now().plusDays(7 + i * 3))
                    .createdBy(demoFaculty)
                    .build());
        }

        String[][] announcements = {
            {"Mid-Semester Exam Schedule Released", "The mid-semester timetable is published on the portal. Exams begin Monday, 25th August. Report any clashes to your department coordinator by Friday.", "https://picsum.photos/seed/examschedule/600/300"},
            {"Library Timings Extended for Exam Season", "The central library will remain open until 11 PM on weekdays during the exam period, starting this week.", "https://picsum.photos/seed/library/600/300"},
            {"Scholarship Applications Open", "Merit and need-based scholarship applications for this academic year are now open. Deadline: end of this month. Apply through the student portal.", "https://picsum.photos/seed/scholarship/600/300"},
            {"Campus Wi-Fi Maintenance Notice", "Wi-Fi services in Blocks A and B will be temporarily unavailable this Saturday from 6 AM to 10 AM for scheduled maintenance.", "https://picsum.photos/seed/wifi/600/300"},
            {"New Elective Courses Added", "Three new electives have been added for the upcoming semester: Cloud Computing, UI/UX Design, and Data Privacy Law. Registration opens next week.", "https://picsum.photos/seed/electives/600/300"},
            {"Holiday Notice - Festival Break", "The college will remain closed for the festival break. Classes resume the following Monday as per the academic calendar.", "https://picsum.photos/seed/holiday/600/300"},
        };

        for (String[] a : announcements) {
            announcementRepository.save(Announcement.builder()
                    .title(a[0])
                    .content(a[1])
                    .posterUrl(a[2])
                    .postedBy(demoFaculty)
                    .build());
        }
    }
}
