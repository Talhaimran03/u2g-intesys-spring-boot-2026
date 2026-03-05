package org.u2g.codylab.teamboard.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
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
import org.u2g.codylab.teamboard.dto.CreateProjectRequestApiDTO;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.repository.UserRepository;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@WithMockUser(username = "testuser")
@AutoConfigureMockMvc
@Transactional
class ProjectIntegrationTest {

    private static final String BASE_URL = "/api/project";

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
                .setPassword("wef7429")
                .setEmail("test@gmail.com")
                .setName("Test")
                .setSurname("User");
        userRepository.save(user);
    }

    @Nested
    class CreateProject {

        @Test
        void shouldCreateProjectSuccessfully() throws Exception {
            CreateProjectRequestApiDTO dto = new CreateProjectRequestApiDTO()
                    .title("Demo")
                    .description("Description");

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("Demo"));
        }

        @Test
        void shouldReturn400ForInvalidRequest() throws Exception {
            CreateProjectRequestApiDTO dto = new CreateProjectRequestApiDTO()
                    .title(null)
                    .description("Description");

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        // TODO: fix the id problem
        @Test
        void shouldCreateProjectWithMembers() throws Exception {
            User user1 = new User()
                    .setUsername("user1")
                    .setPassword("wef7429")
                    .setEmail("user1@gmail.com")
                    .setName("Test")
                    .setSurname("User");
            Long user1Id = userRepository.save(user1).getId();

            User user2 = new User()
                    .setUsername("user2")
                    .setPassword("wef7429")
                    .setEmail("user2@gmail.com")
                    .setName("Test")
                    .setSurname("User");
            Long user2Id = userRepository.save(user2).getId();

            CreateProjectRequestApiDTO dto = new CreateProjectRequestApiDTO()
                    .title("Demo members")
                    .description("Description")
                    .members(List.of(user1Id, user2Id));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("Demo members"))
                    .andExpect(jsonPath("$.members").isArray())
                    .andExpect(jsonPath("$.members.length()").value(2));
        }

        @Test
        void shouldReturn400ForNotFoundMembers() throws Exception {
            CreateProjectRequestApiDTO dto = new CreateProjectRequestApiDTO()
                    .title("Demo members")
                    .description("Description")
                    .members(List.of(999L));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class GetProjects {
        @Test
        void shouldReturnAllProjects() throws Exception {
            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk());
        }
    }


}
