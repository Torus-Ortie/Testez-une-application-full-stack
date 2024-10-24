package com.openclassrooms.starterjwt.controllers;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private Authentication auth;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsImpl userDetails;

    @Test
    public void testAuthenticateUser() throws Exception {
        User user = new User(
            "user@studio.com",
            "Studio",
            "User",
            "test!1234",
            true
        );

        userDetails = new UserDetailsImpl(
            1L,
            "user@studio.com",
            "User",
            "Studio",
            true,
            "test!1234"
        );

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@studio.com");
        loginRequest.setPassword("test!1234");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(loginRequest);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtUtils.generateJwtToken(auth)).thenReturn("JWTToken");
        when(auth.getPrincipal()).thenReturn(userDetails);

        mockMvc.perform(post("/api/auth/login")
        .contentType("application/json")
        .content(jsonRequest))
        .andExpect(status().isOk());

        // Verify isAdmin
        verify(userRepository).findByEmail("user@studio.com");
        assertTrue(user.isAdmin());

        // Verify interaction with authenticationManager
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("user@studio.com", "test!1234"));
    }

    @Test
    public void testRegisterUser() throws Exception {
        // Arrange
        // Création d'une nouvelle demande d'inscription
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("user@studio.com");
        signupRequest.setPassword("test!1234");
        signupRequest.setFirstName("User");
        signupRequest.setLastName("Studio");
        // Conversion de l'objet SignupRequest en chaîne JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(signupRequest);
        // Simulation du comportement de userRepository.existsByEmail pour retourner true
        // Cela signifie que l'email est déjà utilisé
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("passwordEncoded");

        // Act
        // Exécution de la requête POST sur "/api/auth/register"
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(jsonRequest))

        // Assert
        // Vérification que le statut de la réponse est 400 (Bad Request)
        // et que le message de la réponse est "Error: Email is already taken!"
                .andExpect(status().isOk())
                .andExpect(content().string("{\"message\":\"User registered successfully!\"}"));
    }

    @Test
    public void testRegisterUser_BadRequest() throws Exception {
        // Arrange
        // Création d'une nouvelle demande d'inscription
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("user@studio.com");
        signupRequest.setPassword("test!1234");
        signupRequest.setFirstName("User");
        signupRequest.setLastName("Studio");
        // Conversion de l'objet SignupRequest en chaîne JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(signupRequest);
        // Simulation du comportement de userRepository.existsByEmail pour retourner true
        // Cela signifie que l'email est déjà utilisé
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act
        // Exécution de la requête POST sur "/api/auth/register"
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(jsonRequest))

        // Assert
        // Vérification que le statut de la réponse est 400 (Bad Request)
        // et que le message de la réponse est "Error: Email is already taken!"
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"Error: Email is already taken!\"}"));
    }
}
