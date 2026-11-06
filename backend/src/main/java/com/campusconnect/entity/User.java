package com.campusconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // BCrypt hashed

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String department;

    // Optional profile details. Existing users remain valid because these are nullable.
    @Column(length = 20)
    private String phone;

    @Column(length = 500)
    private String bio;

    @Column(length = 100)
    private String course;

    @Column(length = 30)
    private String studyYear;

    @Builder.Default
    private boolean enabled = true;
}
