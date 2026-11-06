package com.campusconnect.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:auth-test;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerAndLoginArePublicAndProtectedRoutesStillRequireJwt() throws Exception {
        String email = "auth-flow@campusconnect.edu";
        String password = "test-password";
        String registerRequest = """
                {
                  "fullName": "Auth Flow",
                  "email": "auth-flow@campusconnect.edu",
                  "password": "test-password",
                  "role": "STUDENT",
                  "department": "Computer Science"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(isEmptyOrNullString())));

        mockMvc.perform(post("/api/auth/login")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(isEmptyOrNullString())));

        mockMvc.perform(get("/api/questions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void corsAllowsTheReactDevelopmentOrigin() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "missing@campusconnect.edu",
                                  "password": "wrong"
                                }
                                """))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }
}
