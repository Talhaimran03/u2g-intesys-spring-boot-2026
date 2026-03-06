package org.u2g.codylab.teamboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.u2g.codylab.teamboard.dto.CreateCardRequestApiDTO;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testuser")
@Transactional
class CardControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_CARD = "/api/card";

    @Test
    void shouldReturn400WhenTitleMissing() throws Exception {
        CreateCardRequestApiDTO request = new CreateCardRequestApiDTO();
        mockMvc.perform(post(API_CARD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenColumnIdMissing() throws Exception {
        CreateCardRequestApiDTO request = new CreateCardRequestApiDTO();
        request.setTitle("Test Card");
        mockMvc.perform(post(API_CARD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenTitleAndColumnIdMissing() throws Exception {
        CreateCardRequestApiDTO request = new CreateCardRequestApiDTO();
        mockMvc.perform(post(API_CARD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

