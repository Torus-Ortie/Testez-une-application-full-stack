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

import static com.openclassrooms.starterjwt.utils.TestConstant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Long MOCK_USER_ID = 1L;
    private static final Long NON_EXISTING_USER_ID = 2L;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User(
                MOCK_USER_ID,
                TEST_USER_EMAIL,
                TEST_USER_LAST_NAME,
                TEST_USER_FIRST_NAME,
                TEST_USER_PASSWORD,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Delete User")
    void shouldDeleteUserWhenDeleteIsCalled() {
        userService.delete(MOCK_USER_ID);

        verify(userRepository).deleteById(MOCK_USER_ID);
    }

    @Test
    @DisplayName("Find User By Existing ID")
    void shouldReturnUserWhenFindByIdIsCalledWithExistingId() {
        when(userRepository.findById(MOCK_USER_ID)).thenReturn(Optional.of(mockUser));

        User actualUser = userService.findById(MOCK_USER_ID);

        assertEquals(mockUser, actualUser);
    }

    @Test
    @DisplayName("Find User By Non-Existing ID")
    void shouldReturnNullWhenFindByIdIsCalledWithNonExistingId() {
        when(userRepository.findById(NON_EXISTING_USER_ID)).thenReturn(Optional.empty());

        User actualUser = userService.findById(NON_EXISTING_USER_ID);

        assertNull(actualUser);
    }
}