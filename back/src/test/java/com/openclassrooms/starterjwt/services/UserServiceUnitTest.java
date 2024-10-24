package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User(
            1L,
            "user@studio.com",
            "Studio",
            "User",
            "test!1234",
            false,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Delete User")
    void shouldDeleteUserWhenDeleteIsCalled() {
        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Find User By Existing ID")
    void shouldReturnUserWhenFindByIdIsCalledWithExistingId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        User actualUser = userService.findById(1L);

        assertEquals(mockUser, actualUser);
    }

    @Test
    @DisplayName("Find User By Non-Existing ID")
    void shouldReturnNullWhenFindByIdIsCalledWithNonExistingId() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        User actualUser = userService.findById(2L);

        assertNull(actualUser);
    }
}