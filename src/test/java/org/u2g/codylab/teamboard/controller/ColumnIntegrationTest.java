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
import org.u2g.codylab.teamboard.dto.CreateColumnRequestApiDTO;
import org.u2g.codylab.teamboard.dto.UpdateColumnRequestApiDTO;
import org.u2g.codylab.teamboard.entity.Column;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.entity.User;
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
class ColumnIntegrationTest {

    private static final String BASE_URL = "/api/column";

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

    private Long projectId;

    @BeforeEach
    void setup() {
        User user = new User()
                .setUsername("testuser")
                .setPassword("wef7429")
                .setEmail("test@gmail.com")
                .setName("Test")
                .setSurname("User");
        userRepository.save(user);

        Project project = new Project()
                .setTitle("Test Project")
                .setDescription("Test Description")
                .setOwner(user);
        projectId = projectRepository.save(project).getId();
    }

    @Nested
    class GetAllColumns {

        @Test
        void shouldReturnAllColumns() throws Exception {
            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    class GetColumnById {

        @Test
        void shouldReturnColumnById() throws Exception {
            Column column = new Column()
                    .setTitle("To Do")
                    .setPosition(1);
            Project project = projectRepository.findById(projectId).orElseThrow();
            column.setProject(project);
            Long columnId = columnRepository.save(column).getId();

            mockMvc.perform(get(BASE_URL + "/" + columnId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("To Do"));
        }

        @Test
        void shouldReturn404WhenColumnNotFound() throws Exception {
            mockMvc.perform(get(BASE_URL + "/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class CreateColumn {

        @Test
        void shouldCreateColumnSuccessfully() throws Exception {
            CreateColumnRequestApiDTO dto = new CreateColumnRequestApiDTO()
                    .title("In Progress")
                    .position(1)
                    .projectId(projectId);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("In Progress"));
        }

        @Test
        void shouldReturn404WhenProjectNotFound() throws Exception {
            CreateColumnRequestApiDTO dto = new CreateColumnRequestApiDTO()
                    .title("Done")
                    .position(2)
                    .projectId(999L);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class UpdateColumn {

        @Test
        void shouldUpdateColumnSuccessfully() throws Exception {
            Column column = new Column()
                    .setTitle("Old Title")
                    .setPosition(1);
            Project project = projectRepository.findById(projectId).orElseThrow();
            column.setProject(project);
            Long columnId = columnRepository.save(column).getId();

            UpdateColumnRequestApiDTO dto = new UpdateColumnRequestApiDTO()
                    .title("New Title")
                    .position(2);

            mockMvc.perform(put(BASE_URL + "/" + columnId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("New Title"));
        }

        @Test
        void shouldReturn404WhenColumnNotFoundOnUpdate() throws Exception {
            UpdateColumnRequestApiDTO dto = new UpdateColumnRequestApiDTO()
                    .title("New Title")
                    .position(1);

            mockMvc.perform(put(BASE_URL + "/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeleteColumn {

        @Test
        void shouldDeleteColumnSuccessfully() throws Exception {
            Column column = new Column()
                    .setTitle("To Delete")
                    .setPosition(1);
            Project project = projectRepository.findById(projectId).orElseThrow();
            column.setProject(project);
            Long columnId = columnRepository.save(column).getId();

            mockMvc.perform(delete(BASE_URL + "/" + columnId))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturn404WhenColumnNotFoundOnDelete() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/999"))
                    .andExpect(status().isNotFound());
        }
    }
}