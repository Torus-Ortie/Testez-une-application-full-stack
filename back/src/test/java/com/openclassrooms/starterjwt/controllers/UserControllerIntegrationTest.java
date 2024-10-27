package com.openclassrooms.starterjwt.controllers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc 
@TestPropertySource(locations = "classpath:application-test.properties") 
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) 
public class UserControllerIntegrationTest {

    @Autowired 
    private MockMvc mockMvc;

    @Test 
    @WithMockUser
    public void testFindById_Success() throws Exception {
        mockMvc.perform(get("/api/user/{id}", 2L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.id", is(2))) 
                .andExpect(jsonPath("$.email", is("user@studio.com"))) 
                .andExpect(jsonPath("$.lastName", is("Studio"))) 
                .andExpect(jsonPath("$.firstName", is("User"))); 
    }

    @Test 
    @WithMockUser 
    public void testFindById_UserNotFound() throws Exception {
        mockMvc.perform(get("/api/user/{id}", 999L) 
                .contentType(MediaType.APPLICATION_JSON)) 
                .andExpect(status().isNotFound());
    }

    @Test 
    @WithMockUser(username = "user@studio.com") 
    public void testDelete_Success() throws Exception {
        mockMvc.perform(delete("/api/user/{id}", 2L) 
                .contentType(MediaType.APPLICATION_JSON)) 
                .andExpect(status().isOk()); 
    }

    @Test 
    @WithMockUser(username = "user@studio.com") 
    public void testDelete_UserNotFound() throws Exception {
        mockMvc.perform(delete("/api/user/{id}", 999L) 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
