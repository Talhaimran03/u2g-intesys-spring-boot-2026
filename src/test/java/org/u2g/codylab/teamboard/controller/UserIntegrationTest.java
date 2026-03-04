package org.u2g.codylab.teamboard.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.u2g.codylab.teamboard.dto.RegisterRequestApiDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {

    private static final String BASE_URL = "/api/register";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class Register {

        @Test
        void shouldRegisterUserSuccessfully() throws Exception {

            RegisterRequestApiDTO dto = new RegisterRequestApiDTO()
                    .username("integrationuser")
                    .password("Password@123")
                    .email("test@gmail.com")
                    .name("Test")
                    .surname("Test");

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }

        @Test
        void shouldThrowIllegalArgumentException() throws Exception {

            RegisterRequestApiDTO dto = new RegisterRequestApiDTO()
                    .username("integration&user")
                    .password("Password@123")
                    .email("test@gmail.com")
                    .name("Test")
                    .surname("Test");

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldThrowConflictException() throws Exception {

            RegisterRequestApiDTO dto = new RegisterRequestApiDTO()
                    .username("integrationuser")
                    .password("Password@123")
                    .email("test@gmail.com")
                    .name("Test")
                    .surname("Test");

            // create user
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());

            // try to create user again with same username
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isConflict());
        }

    }
}
