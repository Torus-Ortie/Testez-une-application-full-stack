package com.openclassrooms.starterjwt.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    public void testFindById_Success() throws Exception {
        Long id = 1L;
        String expectedName = "Yoga";
        Long expectedTeacherId = 1L;
        String expectedDescription = "Yoga session 1";
    
        mockMvc.perform(get("/api/session/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(id.intValue())))
            .andExpect(jsonPath("$.name", is(expectedName)))
            .andExpect(jsonPath("$.teacher_id", is(expectedTeacherId.intValue())))
            .andExpect(jsonPath("$.description", is(expectedDescription)));
    }

    @Test
    @WithMockUser
    public void testFindById_SessionNotFound() throws Exception {
        Long id = 9999L;

        mockMvc.perform(get("/api/session/{id}", id))
            .andExpect(status().isNotFound());
    }
    @Test
    @WithMockUser
    public void testFindAll_Integration() throws Exception {
        mockMvc.perform(get("/api/session"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", not(empty())));
    }
    
    @Test
    @WithMockUser
    public void testCreate() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Test Session");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);
        sessionDto.setDescription("This is a test session.");
    
        String sessionDtoJson = new ObjectMapper().writeValueAsString(sessionDto);
    
        mockMvc.perform(post("/api/session")
            .contentType(APPLICATION_JSON)
            .content(sessionDtoJson))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testUpdate() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Updated Session");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);
        sessionDto.setDescription("This is an updated test session.");

        String sessionDtoJson = new ObjectMapper().writeValueAsString(sessionDto);
        Long id = 1L;

        mockMvc.perform(put("/api/session/{id}", id)
            .contentType(APPLICATION_JSON)
            .content(sessionDtoJson))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testDelete() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/api/session/{id}", id))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testParticipate() throws Exception {
        Long sessionId = 1L;
        Long userId = 2L;

        mockMvc.perform(post("/api/session/{id}/participate/{userId}", sessionId, userId))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testNoLongerParticipate() throws Exception {
        Long sessionId = 2L;
        Long userId = 3L;

        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", sessionId, userId))
            .andExpect(status().isOk());
    }
}
