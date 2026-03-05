package org.u2g.codylab.teamboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ActiveProfiles("test")
@WithMockUser(username = "testuser")
@AutoConfigureMockMvc
@Transactional
public class ColumnControllerTest {

    private static final String BASE_URL = "/api/column";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class CreateColumn {

        @Test
        void shouldCreateColumnSuccessfully() throws Exception {

            CreateColumnRequestApiDTO dto =
                    new CreateColumnRequestApiDTO()
                            .title("TO DO");

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("TO DO"));
        }

        @Test
        void shouldReturn400ForInvalidRequest() throws Exception {

            CreateColumnRequestApiDTO dto =
                    new CreateColumnRequestApiDTO()
                            .title(null);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    //get Column
    @Nested
    class GetColumns {

        @Test
        void shouldReturnAllColumns() throws Exception {

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class GetColumnById {

        @Test
        void shouldReturnColumnById() throws Exception {

            CreateColumnRequestApiDTO dto =
                    new CreateColumnRequestApiDTO()
                            .title("IN PROGRESS");

            String response = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long id = objectMapper.readTree(response).get("id").asLong();

            mockMvc.perform(get(BASE_URL + "/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("IN PROGRESS"));
        }
    }

    @Nested
    class UpdateColumn {

        @Test
        void shouldUpdateColumn() throws Exception {

            CreateColumnRequestApiDTO createDto =
                    new CreateColumnRequestApiDTO()
                            .title("TO DO");

            String response = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long id = objectMapper.readTree(response).get("id").asLong();

            UpdateColumnRequestApiDTO updateDto =
                    new UpdateColumnRequestApiDTO()
                            .title("DONE");

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("DONE"));
        }
    }

    @Nested
    class DeleteColumn {

        @Test
        void shouldDeleteColumn() throws Exception {

            CreateColumnRequestApiDTO dto =
                    new CreateColumnRequestApiDTO()
                            .title("DELETE ME");

            String response = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long id = objectMapper.readTree(response).get("id").asLong();

            mockMvc.perform(delete(BASE_URL + "/" + id))
                    .andExpect(status().isNoContent());
        }
    }

}


