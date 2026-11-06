package com.campusconnect.service;

import com.campusconnect.dto.PasswordChangeRequest;
import com.campusconnect.dto.ProfileResponse;
import com.campusconnect.dto.ProfileUpdateRequest;
import com.campusconnect.entity.User;
import com.campusconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String email) {
        return toResponse(findUser(email));
    }

    @Transactional
    public ProfileResponse updateProfile(String email, ProfileUpdateRequest request) {
        User user = findUser(email);
        user.setFullName(request.getFullName().trim());
        user.setPhone(clean(request.getPhone()));
        user.setBio(clean(request.getBio()));
        user.setDepartment(clean(request.getDepartment()));
        user.setCourse(clean(request.getCourse()));
        user.setStudyYear(clean(request.getStudyYear()));
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(String email, PasswordChangeRequest request) {
        User user = findUser(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a password different from your current password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User account not found"));
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private ProfileResponse toResponse(User user) {
        return ProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .department(user.getDepartment())
                .phone(user.getPhone())
                .bio(user.getBio())
                .course(user.getCourse())
                .studyYear(user.getStudyYear())
                .build();
    }
}
