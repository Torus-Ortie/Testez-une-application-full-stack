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
        mockSession.setId(1L);
        mockUser = new User();
        mockUser.setId(1L);
    }

    @Test
    @DisplayName("Create session")
    void shouldCreateSessionWhenCreateIsCalled() {
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Session actualSession = sessionService.create(mockSession);

        verify(sessionRepository).save(mockSession);
        assertEquals(mockSession, actualSession);
    }

    @Test
    @DisplayName("Delete session")
    void shouldDeleteSessionWhenDeleteIsCalled() {
        sessionService.delete(1L);

        verify(sessionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Return all sessions")
    void shouldReturnAllSessionsWhenFindAllIsCalled() {
        Session secondExpectedSession = new Session();
        secondExpectedSession.setId(2L);
        when(sessionRepository.findAll()).thenReturn(Arrays.asList(mockSession, secondExpectedSession));

        List<Session> sessions = sessionService.findAll();

        assertEquals(2, sessions.size());
        assertTrue(sessions.containsAll(Arrays.asList(mockSession, secondExpectedSession)));
    }

    @Test
    @DisplayName("Find Session By Existing ID")
    void shouldReturnSessionWhenGetByIdIsCalledWithExistingId() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSession));

        Session actualSession = sessionService.getById(1L);

        assertEquals(mockSession, actualSession);
    }

    @Test
    @DisplayName("Update Session")
    void shouldUpdateSessionWhenUpdateIsCalled() {
        Session updatedSession = new Session();
        updatedSession.setId(1L);
        when(sessionRepository.save(updatedSession)).thenReturn(updatedSession);

        Session actualSession = sessionService.update(1L, updatedSession);

        verify(sessionRepository).save(updatedSession);
        assertEquals(updatedSession, actualSession);
    }

    @Test
    @DisplayName("Add User to Session")
    void shouldAddUserToSessionWhenParticipateIsCalledWithExistingSessionAndUserId() {
        mockSession.setUsers(new ArrayList<>());
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSession));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        sessionService.participate(1L, 1L);

        verify(sessionRepository).findById(1L);
        verify(userRepository).findById(1L);
        assertTrue(mockSession.getUsers().contains(mockUser));
        verify(sessionRepository).save(mockSession);
    }

    @Test
    @DisplayName("Remove User from Session")
    void shouldRemoveUserFromSessionWhenNoLongerParticipateIsCalledWithExistingSessionAndUserId() {
        User secondExpectedUser = new User();
        secondExpectedUser.setId(2L);
        mockSession.setUsers(Arrays.asList(mockUser, secondExpectedUser));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSession));

        sessionService.noLongerParticipate(1L, 1L);

        verify(sessionRepository).findById(1L);
        assertEquals(1, mockSession.getUsers().size());
        assertTrue(mockSession.getUsers().contains(secondExpectedUser));
        verify(sessionRepository).save(mockSession);
    }

    @Test
    @DisplayName("Participate - Session Not Found")
    void shouldThrowNotFoundExceptionWhenParticipateIsCalledWithNonExistingSessionId() {
        when(sessionRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(2L, 1L));
    }

    @Test
    @DisplayName("Participate - User Not Found")
    void shouldThrowNotFoundExceptionWhenParticipateIsCalledWithExistingSessionIdAndNonExistingUserId() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSession));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 2L));
    }

    @Test
    @DisplayName("No Longer Participate - Session Not Found")
    void shouldThrowNotFoundExceptionWhenNoLongerParticipateIsCalledWithNonExistingSessionId() {
        when(sessionRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(2L, 1L));
    }

    @Test
    @DisplayName("Participate - User Already in Session")
    void shouldThrowBadRequestExceptionWhenParticipateIsCalledWithExistingSessionIdAndUserAlreadyInSession() {
        mockSession.setUsers(Collections.singletonList(mockUser));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSession));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 1L));
    }
    @Test
    @DisplayName("No Longer Participate - User Not in Session")
    void shouldThrowBadRequestExceptionWhenNoLongerParticipateIsCalledWithExistingSessionIdAndUserNotInSession() {
        mockSession.setUsers(new ArrayList<>());
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSession));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 1L));
    }

}