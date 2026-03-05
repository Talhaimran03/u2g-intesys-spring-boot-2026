package org.u2g.codylab.teamboard.controller;

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
import org.u2g.codylab.teamboard.dto.UpdateCardRequestApiDTO;
import org.u2g.codylab.teamboard.entity.Card;
import org.u2g.codylab.teamboard.entity.Column;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.repository.CardRepository;
import org.u2g.codylab.teamboard.repository.ColumnRepository;
import org.u2g.codylab.teamboard.repository.ProjectRepository;
import org.u2g.codylab.teamboard.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ColumnRepository columnRepository;
    @Autowired
    private CardRepository cardRepository;

    private Long columnId;
    private Long userId;

    @BeforeEach
    void setup() {
        User user = new User()
                .setUsername("testuser")
                .setPassword("wef7429")
                .setEmail("test@gmail.com")
                .setName("Test")
                .setSurname("User");
        userId = userRepository.save(user).getId();

        Project project = new Project()
                .setTitle("Test Project")
                .setDescription("Test Description")
                .setOwner(user);
        projectRepository.save(project);

        Column column = new Column()
                .setTitle("To Do")
                .setPosition(1)
                .setProject(project);
        columnId = columnRepository.save(column).getId();
    }

    @Nested
    class GetCardById {

        @Test
        void shouldReturnCardById() throws Exception {
            User user = userRepository.findById(userId).orElseThrow();
            Column column = columnRepository.findById(columnId).orElseThrow();
            Card card = new Card()
                    .setTitle("My Card")
                    .setDescription("Card description")
                    .setAssignedTo(user)
                    .setColumn(column);
            Long cardId = cardRepository.save(card).getId();

            mockMvc.perform(get(BASE_URL + "/" + cardId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("My Card"));
        }

        @Test
        void shouldReturn404WhenCardNotFound() throws Exception {
            mockMvc.perform(get(BASE_URL + "/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class CreateCard {

        @Test
        void shouldCreateCardSuccessfully() throws Exception {
            CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO()
                    .title("New Card")
                    .description("Description")
                    .columnId(columnId)
                    .assignedToId(userId);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("New Card"));
        }

        @Test
        void shouldReturn404WhenColumnNotFound() throws Exception {
            CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO()
                    .title("New Card")
                    .description("Description")
                    .columnId(999L)
                    .assignedToId(userId);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturn404WhenUserNotFound() throws Exception {
            CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO()
                    .title("New Card")
                    .description("Description")
                    .columnId(columnId)
                    .assignedToId(999L);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class UpdateCard {

        @Test
        void shouldUpdateCardSuccessfully() throws Exception {
            User user = userRepository.findById(userId).orElseThrow();
            Column column = columnRepository.findById(columnId).orElseThrow();
            Card card = new Card()
                    .setTitle("Old Title")
                    .setDescription("Old Desc")
                    .setAssignedTo(user)
                    .setColumn(column);
            Long cardId = cardRepository.save(card).getId();

            UpdateCardRequestApiDTO dto = new UpdateCardRequestApiDTO()
                    .title("New Title")
                    .description("New Desc");

            mockMvc.perform(put(BASE_URL + "/" + cardId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("New Title"))
                    .andExpect(jsonPath("$.description").value("New Desc"));
        }

        @Test
        void shouldReturn404WhenCardNotFoundOnUpdate() throws Exception {
            UpdateCardRequestApiDTO dto = new UpdateCardRequestApiDTO()
                    .title("New Title");

            mockMvc.perform(put(BASE_URL + "/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeleteCard {

        @Test
        void shouldDeleteCardSuccessfully() throws Exception {
            User user = userRepository.findById(userId).orElseThrow();
            Column column = columnRepository.findById(columnId).orElseThrow();
            Card card = new Card()
                    .setTitle("To Delete")
                    .setDescription("desc")
                    .setAssignedTo(user)
                    .setColumn(column);
            Long cardId = cardRepository.save(card).getId();

            mockMvc.perform(delete(BASE_URL + "/" + cardId))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturn404WhenCardNotFoundOnDelete() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/999"))
                    .andExpect(status().isNotFound());
        }
    }
}
