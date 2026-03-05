package org.u2g.codylab.teamboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.u2g.codylab.teamboard.dto.CreateCardRequestApiDTO;
import org.u2g.codylab.teamboard.dto.CreateColumnRequestApiDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@WithMockUser(username = "testuser")
@AutoConfigureMockMvc
@Transactional
public class CardControllerTest {

    private static final String CARD_BASE_URL = "/api/card";
    private static final String COLUMN_BASE_URL = "/api/column";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Helper: create a Column and return its ID
     */
    private Long createColumn(String title) throws Exception {
        CreateColumnRequestApiDTO columnDto = new CreateColumnRequestApiDTO().title(title);

        String response = mockMvc.perform(post(COLUMN_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(columnDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    // =====================================
    // Nested class for CRUD Tests
    // =====================================
    @Nested
    class CRUDTests {

        @Test
        void shouldCreateCardSuccessfully() throws Exception {
            Long columnId = createColumn("TO DO");

            CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO()
                    .title("Task 1")
                    .description("First task")
                    .columnId(columnId);

            mockMvc.perform(post(CARD_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("Task 1"))
                    .andExpect(jsonPath("$.columnId").value(columnId));
        }

        @Test
        void shouldReturnCardById() throws Exception {
            Long columnId = createColumn("IN PROGRESS");

            CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO()
                    .title("Read Docs")
                    .description("Integration test")
                    .columnId(columnId);

            String response = mockMvc.perform(post(CARD_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long cardId = objectMapper.readTree(response).get("id").asLong();

            mockMvc.perform(get(CARD_BASE_URL + "/" + cardId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Read Docs"))
                    .andExpect(jsonPath("$.columnId").value(columnId));
        }

        @Test
        void shouldUpdateCard() throws Exception {
            Long columnId = createColumn("TO DO");

            CreateCardRequestApiDTO createDto = new CreateCardRequestApiDTO()
                    .title("Initial Task")
                    .description("Before update")
                    .columnId(columnId);

            String response = mockMvc.perform(post(CARD_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long cardId = objectMapper.readTree(response).get("id").asLong();

            CreateCardRequestApiDTO updateDto = new CreateCardRequestApiDTO()
                    .title("Updated Task")
                    .description("After update")
                    .columnId(columnId);

            mockMvc.perform(put(CARD_BASE_URL + "/" + cardId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Task"))
                    .andExpect(jsonPath("$.columnId").value(columnId));
        }

        @Test
        void shouldDeleteCard() throws Exception {
            Long columnId = createColumn("DONE");

            CreateCardRequestApiDTO createDto = new CreateCardRequestApiDTO()
                    .title("Task to delete")
                    .description("Delete me")
                    .columnId(columnId);

            String response = mockMvc.perform(post(CARD_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long cardId = objectMapper.readTree(response).get("id").asLong();

            mockMvc.perform(delete(CARD_BASE_URL + "/" + cardId))
                    .andExpect(status().isNoContent());
        }
    }

    // =====================================
    // Nested class for Validation Tests
    // =====================================
    @Nested
    class ValidationTests {

        @Test
        void shouldReturn400WhenTitleMissing() throws Exception {
            Long columnId = createColumn("IN PROGRESS");

            CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO()
                    .title(null)
                    .description("No title")
                    .columnId(columnId);

            mockMvc.perform(post(CARD_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn400WhenColumnIdMissing() throws Exception {
            CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO()
                    .title("Task without column")
                    .description("Missing columnId");

            mockMvc.perform(post(CARD_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }
}
