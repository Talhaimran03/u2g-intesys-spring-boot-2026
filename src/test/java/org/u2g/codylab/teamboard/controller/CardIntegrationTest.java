package org.u2g.codylab.teamboard.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.u2g.codylab.teamboard.dto.CreateCardRequestApiDTO;
import org.u2g.codylab.teamboard.dto.CreateColumnRequestApiDTO;
import org.u2g.codylab.teamboard.dto.CreateProjectRequestApiDTO;
import org.u2g.codylab.teamboard.dto.UpdateCardRequestApiDTO;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@WithMockUser(username = "testuser")
@AutoConfigureMockMvc
@Transactional
class CardIntegrationTest {

    private static final String BASE_URL = "/api/card";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        User user = new User()
                .setUsername("testuser")
                .setPassword("password")
                .setEmail("test@gmail.com")
                .setName("Test")
                .setSurname("User");

        userRepository.save(user);
    }


    //    Create Project → Column → return columnId
    private Long createColumn() throws Exception {

        CreateProjectRequestApiDTO projectDto = new CreateProjectRequestApiDTO()
                .title("Project Test")
                .description("Description");

        String projectResponse = mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode projectJson = objectMapper.readTree(projectResponse);
        Long projectId = projectJson.get("id").asLong();


        CreateColumnRequestApiDTO columnDto = new CreateColumnRequestApiDTO()
                .title("Todo")
                .position(1)
                .projectId(projectId);

        String columnResponse = mockMvc.perform(post("/api/column")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(columnDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode columnJson = objectMapper.readTree(columnResponse);
        return columnJson.get("id").asLong();
    }

    //    Create Card → return cardId
    private Long createCard() throws Exception {

        Long columnId = createColumn();

        CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO()
                .title("Card Test")
                .description("Description")
                .columnId(columnId);

        String response = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return json.get("id").asLong();
    }

    //    CREATE CARD
    @Nested
    class CreateCard {

        @Test
        void shouldCreateCardSuccessfully() throws Exception {

            Long columnId = createColumn();

            CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO()
                    .title("My Card")
                    .description("Description")
                    .columnId(columnId);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("My Card"));
        }

        @Test
        void shouldReturn400ForInvalidRequest() throws Exception {

            CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO()
                    .title(null);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

       // GET CARD
    @Nested
    class GetCard {

        @Test
        void shouldReturnCardById() throws Exception {

            Long cardId = createCard();

            mockMvc.perform(get(BASE_URL + "/" + cardId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(cardId));
        }
    }


    //    UPDATE CARD
    @Nested
    class UpdateCard {

        @Test
        void shouldUpdateCardSuccessfully() throws Exception {

            Long cardId = createCard();

            UpdateCardRequestApiDTO dto = new UpdateCardRequestApiDTO()
                    .title("Updated Card")
                    .description("Updated description");

            mockMvc.perform(put(BASE_URL + "/" + cardId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Card"));
        }
    }


    //    DELETE CARD
    @Nested
    class DeleteCard {

        @Test
        void shouldDeleteCardSuccessfully() throws Exception {

            Long cardId = createCard();

            mockMvc.perform(delete(BASE_URL + "/" + cardId))
                    .andExpect(status().isNoContent());
        }
    }
}