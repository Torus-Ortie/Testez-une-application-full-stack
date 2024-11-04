package com.openclassrooms.starterjwt.controllers;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testLogin_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user1@studio.com");
        loginRequest.setPassword("password");

        String jsonLoginRequest = new ObjectMapper().writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonLoginRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", is(notNullValue())));
    }

    @Test
    public void testLogin_Unauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("wrong@studio.com");
        loginRequest.setPassword("wrongpassword");

        String jsonLoginRequest = new ObjectMapper().writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonLoginRequest))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRegister_Success() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("new@studio.com");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("studio");
        signupRequest.setPassword("password");

        String jsonSignupRequest = new ObjectMapper().writeValueAsString(signupRequest);

        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonSignupRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", is("User registered successfully!")));
    }

    @Test
    public void testRegister_BadRequest() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("invalid");
        signupRequest.setPassword("123");

        String jsonSignupRequest = new ObjectMapper().writeValueAsString(signupRequest);

        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonSignupRequest))
            .andExpect(status().isBadRequest());
    }
}