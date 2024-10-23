package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    private static final Long EXISTING_SESSION_ID = 1L;
    private static final Long NON_EXISTING_SESSION_ID = 2L;
    private static final Long EXISTING_USER_ID = 1L;
    private static final Long NON_EXISTING_USER_ID = 2L;

    @InjectMocks
    private SessionService sessionService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    private Session mockSession;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockSession = new Session();
        mockSession.setId(EXISTING_SESSION_ID);
        mockUser = new User();
        mockUser.setId(EXISTING_USER_ID);
    }

    @Test
    @DisplayName("Create session")
    void shouldCreateSessionWhenCreateIsCalled() {
        // Arrange
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Session actualSession = sessionService.create(mockSession);

        // Assert
        verify(sessionRepository).save(mockSession);
        assertEquals(mockSession, actualSession);
    }

    @Test
    @DisplayName("Delete session")
    void shouldDeleteSessionWhenDeleteIsCalled() {
        // Act
        sessionService.delete(EXISTING_SESSION_ID);

        // Assert
        verify(sessionRepository).deleteById(EXISTING_SESSION_ID);
    }

    @Test
    @DisplayName("Return all sessions")
    void shouldReturnAllSessionsWhenFindAllIsCalled() {
        // Arrange
        Session secondExpectedSession = new Session();
        secondExpectedSession.setId(2L);
        when(sessionRepository.findAll()).thenReturn(Arrays.asList(mockSession, secondExpectedSession));

        // Act
        List<Session> sessions = sessionService.findAll();

        // Assert
        assertEquals(2, sessions.size());
        assertTrue(sessions.containsAll(Arrays.asList(mockSession, secondExpectedSession)));
    }

    @Test
    @DisplayName("Find Session By Existing ID")
    void shouldReturnSessionWhenGetByIdIsCalledWithExistingId() {
        // Arrange
        when(sessionRepository.findById(EXISTING_SESSION_ID)).thenReturn(Optional.of(mockSession));

        // Act
        Session actualSession = sessionService.getById(EXISTING_SESSION_ID);

        // Assert
        assertEquals(mockSession, actualSession);
    }

    @Test
    @DisplayName("Update Session")
    void shouldUpdateSessionWhenUpdateIsCalled() {
        // Arrange
        Session updatedSession = new Session();
        updatedSession.setId(EXISTING_SESSION_ID);
        when(sessionRepository.save(updatedSession)).thenReturn(updatedSession);

        // Act
        Session actualSession = sessionService.update(EXISTING_SESSION_ID, updatedSession);

        // Assert
        verify(sessionRepository).save(updatedSession);
        assertEquals(updatedSession, actualSession);
    }

    @Test
    @DisplayName("Add User to Session")
    void shouldAddUserToSessionWhenParticipateIsCalledWithExistingSessionAndUserId() {
        // Arrange
        mockSession.setUsers(new ArrayList<>());
        when(sessionRepository.findById(EXISTING_SESSION_ID)).thenReturn(Optional.of(mockSession));
        when(userRepository.findById(EXISTING_USER_ID)).thenReturn(Optional.of(mockUser));

        // Act
        sessionService.participate(EXISTING_SESSION_ID, EXISTING_USER_ID);

        // Assert
        verify(sessionRepository).findById(EXISTING_SESSION_ID);
        verify(userRepository).findById(EXISTING_USER_ID);
        assertTrue(mockSession.getUsers().contains(mockUser));
        verify(sessionRepository).save(mockSession);
    }

    @Test
    @DisplayName("Remove User from Session")
    void shouldRemoveUserFromSessionWhenNoLongerParticipateIsCalledWithExistingSessionAndUserId() {
        // Arrange
        User secondExpectedUser = new User();
        secondExpectedUser.setId(2L);
        mockSession.setUsers(Arrays.asList(mockUser, secondExpectedUser));
        when(sessionRepository.findById(EXISTING_SESSION_ID)).thenReturn(Optional.of(mockSession));

        // Act
        sessionService.noLongerParticipate(EXISTING_SESSION_ID, EXISTING_USER_ID);

        // Assert
        verify(sessionRepository).findById(EXISTING_SESSION_ID);
        assertEquals(1, mockSession.getUsers().size());
        assertTrue(mockSession.getUsers().contains(secondExpectedUser));
        verify(sessionRepository).save(mockSession);
    }

    @Test
    @DisplayName("Participate - Session Not Found")
    void shouldThrowNotFoundExceptionWhenParticipateIsCalledWithNonExistingSessionId() {
        // Arrange
        when(sessionRepository.findById(NON_EXISTING_SESSION_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> sessionService.participate(NON_EXISTING_SESSION_ID, EXISTING_USER_ID));
    }

    @Test
    @DisplayName("Participate - User Not Found")
    void shouldThrowNotFoundExceptionWhenParticipateIsCalledWithExistingSessionIdAndNonExistingUserId() {
        // Arrange
        when(sessionRepository.findById(EXISTING_SESSION_ID)).thenReturn(Optional.of(mockSession));
        when(userRepository.findById(NON_EXISTING_USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> sessionService.participate(EXISTING_SESSION_ID, NON_EXISTING_USER_ID));
    }

    @Test
    @DisplayName("No Longer Participate - Session Not Found")
    void shouldThrowNotFoundExceptionWhenNoLongerParticipateIsCalledWithNonExistingSessionId() {
        // Arrange
        when(sessionRepository.findById(NON_EXISTING_SESSION_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(NON_EXISTING_SESSION_ID, EXISTING_USER_ID));
    }

    @Test
    @DisplayName("Participate - User Already in Session")
    void shouldThrowBadRequestExceptionWhenParticipateIsCalledWithExistingSessionIdAndUserAlreadyInSession() {
        // Arrange
        mockSession.setUsers(Collections.singletonList(mockUser));
        when(sessionRepository.findById(EXISTING_SESSION_ID)).thenReturn(Optional.of(mockSession));
        when(userRepository.findById(EXISTING_USER_ID)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> sessionService.participate(EXISTING_SESSION_ID, EXISTING_USER_ID));
    }
    @Test
    @DisplayName("No Longer Participate - User Not in Session")
    void shouldThrowBadRequestExceptionWhenNoLongerParticipateIsCalledWithExistingSessionIdAndUserNotInSession() {
        // Arrange
        mockSession.setUsers(new ArrayList<>());
        when(sessionRepository.findById(EXISTING_SESSION_ID)).thenReturn(Optional.of(mockSession));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(EXISTING_SESSION_ID, EXISTING_USER_ID));
    }

}