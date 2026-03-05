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
import org.u2g.codylab.teamboard.dto.CreateProjectRequestApiDTO;
import org.u2g.codylab.teamboard.dto.UpdateColumnRequestApiDTO;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.repository.UserRepository;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    // CREATE COLUMN
    @Nested
    class CreateColumn {

        @Test
        void shouldCreateColumnSuccessfully() throws Exception {

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

            Long projectId = objectMapper.readTree(projectResponse).get("id").asLong();

            CreateColumnRequestApiDTO columnDto = new CreateColumnRequestApiDTO()
                    .title("In Progress")
                    .position(1)
                    .projectId(projectId);

            String columnResponse = mockMvc.perform(post("/api/column")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(columnDto)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long columnId = objectMapper.readTree(columnResponse).get("id").asLong();

            mockMvc.perform(get("/api/column/" + columnId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("In Progress"));
        }

        @Test
        void shouldReturn400ForInvalidRequest() throws Exception {

            CreateColumnRequestApiDTO dto = new CreateColumnRequestApiDTO()
                    .title(null);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    // GET COLUMN
    @Nested
    class GetColumn {

        @Test
        void shouldReturnColumnById() throws Exception {
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

            Long projectId = objectMapper.readTree(projectResponse).get("id").asLong();

            CreateColumnRequestApiDTO columnDto = new CreateColumnRequestApiDTO()
                    .title("In Progress")
                    .position(1)
                    .projectId(projectId);

            String columnResponse = mockMvc.perform(post("/api/column")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(columnDto)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long columnId = objectMapper.readTree(columnResponse).get("id").asLong();

            mockMvc.perform(get("/api/column/" + columnId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("In Progress"));
        }

        @Test
        void shouldReturn404WhenColumnNotFound() throws Exception {

            mockMvc.perform(get(BASE_URL + "/999"))
                    .andExpect(status().isNotFound());
        }
    }

    // UPDATE COLUMN
    @Nested
    class UpdateColumn {

        @Test
        void shouldUpdateColumnSuccessfully() throws Exception {
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

            Long projectId = objectMapper.readTree(projectResponse).get("id").asLong();

            CreateColumnRequestApiDTO createDto = new CreateColumnRequestApiDTO()
                    .title("Todo");CreateColumnRequestApiDTO columnDto = new CreateColumnRequestApiDTO()
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

            Long columnId = objectMapper.readTree(columnResponse).get("id").asLong();


            UpdateColumnRequestApiDTO updateDto = new UpdateColumnRequestApiDTO()
                    .title("Updated Todo")
                    .position(2);

            mockMvc.perform(put("/api/column/" + columnId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Todo"));
        }

        @Test
        void shouldReturn404WhenUpdatingNonExistingColumn() throws Exception {

            UpdateColumnRequestApiDTO updateDto = new UpdateColumnRequestApiDTO()
                    .title("Updated");

            mockMvc.perform(put(BASE_URL + "/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isNotFound());
        }
    }


    // DELETE COLUMN
    @Nested
    class DeleteColumn {

        @Test
        void shouldDeleteColumnSuccessfully() throws Exception {
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

            Long projectId = objectMapper.readTree(projectResponse).get("id").asLong();

            CreateColumnRequestApiDTO columnDto = new CreateColumnRequestApiDTO()
                    .title("To Delete")
                    .position(1)
                    .projectId(projectId);

            String columnResponse = mockMvc.perform(post("/api/column")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(columnDto)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long columnId = objectMapper.readTree(columnResponse).get("id").asLong();

            mockMvc.perform(delete("/api/column/" + columnId))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturn404WhenDeletingNonExistingColumn() throws Exception {

            mockMvc.perform(delete(BASE_URL + "/999"))
                    .andExpect(status().isNotFound());
        }
    }

    // GET ALL COLUMNS
    @Nested
    class GetAllColumns {

        @Test
        void shouldReturnAllColumns() throws Exception {

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk());
        }
    }
}