package com.xw.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.xw.api.common.UserRole;
import com.xw.api.dto.UserRequest;
import com.xw.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataInitializationService {

    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${app.superuser.email:root@example.com}")
    private String superUserEmail;

    @Value("${app.superuser.username:root}")
    private String superUserUsername;

    @Value("${app.superuser.password:root123}")
    private String superUserPassword;

    @EventListener(ApplicationReadyEvent.class)
    public void createSuperUserIfNotExists() {
        try {
            // Check if any root user exists
            boolean rootExists = userRepository.existsRootUser();

            if (!rootExists) {
                log.info("No root user found. Creating default super user...");

                UserRequest superUserRequest = UserRequest.builder()
                        .userEmail(superUserEmail)
                        .username(superUserUsername)
                        .password(superUserPassword)
                        .role(UserRole.ROLE_ROOT.name())
                        .build();

                userService.createInitialSuperUser(superUserRequest);
                log.info("Super user created successfully with email: {}", superUserEmail);
                log.warn("SECURITY WARNING: Default super user created. Please change the password immediately!");
                log.warn("Default credentials - Email: {}, Password: {}", superUserEmail, superUserPassword);
            } else {
                log.info("Admin user already exists. Skipping super user creation.");
            }
        } catch (Exception e) {
            log.error("Failed to create super user: {}", e.getMessage(), e);
        }
    }
}