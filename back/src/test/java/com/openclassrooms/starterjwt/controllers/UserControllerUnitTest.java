package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.services.UserService;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;
    private UserDto userDto;

    @BeforeEach
    public void setup() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName("Yoga");
        userDto.setLastName("Studio");

        User user = new User();
        user.setId(1L);
        user.setFirstName("Yoga");
        user.setLastName("Studio");

        List<UserDto> userDtos = Arrays.asList(userDto);

        List<User> users = Arrays.asList(user);

        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(userMapper.toDto(users)).thenReturn(userDtos);
    }

    @Test
    @WithMockUser
    public void testFindById() throws Exception {
        mockMvc.perform(get("/api/user/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.firstName", is("Yoga")))
        .andExpect(jsonPath("$.lastName", is("Studio")));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser
    public void testFindById_NumberFormatException() throws Exception {
        mockMvc.perform(get("/api/user/notANumber"))
        .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "yoga@studio.com")
    public void testDeleteById() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("yoga@studio.com");

        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(delete("/api/user/1"))
        .andExpect(status().isOk());

        verify(userService, times(1)).delete(1L);
    }

    @Test
    @WithMockUser(username = "user@studio.com")
    public void testDeleteById_Unauthorized() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("yoga@studio.com");

        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(delete("/api/user/1"))
        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testDeleteById_NumberFormatException() throws Exception {
        mockMvc.perform(delete("/api/user/notANumber"))
        .andExpect(status().isBadRequest());
    }
}