package com.openclassrooms.starterjwt.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.SessionService;


@SpringBootTest
@AutoConfigureMockMvc
public class SessionControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionController sessionController;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionMapper sessionMapper;

    private Session session;
    private SessionDto sessionDto;
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        session = new Session();
        session.setId(1L);

        sessionDto = new SessionDto();
        sessionDto.setId(1L);

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private void initializeSession() {
        session.setName("Test Session");
        session.setDate(new Date());
        session.setTeacher(new Teacher(1L, "DELAHAYE", "Margot", null, null));
        session.setDescription("This is a test session.");
        session.setUsers(Arrays.asList(
            new User(2L, "user1@studio.com", "Studio", "User1", "test!1234", false, null, null),
            new User(3L, "user2@studio.com", "Studio", "User2", "test!12345", false, null, null)
        ));
    }

    private void initializeDto() {
        sessionDto.setName("Test Session");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);
        sessionDto.setDescription("Test Description");
        sessionDto.setUsers(Arrays.asList(1L, 2L));
        sessionDto.setCreatedAt(LocalDateTime.now());
        sessionDto.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser
    public void testFindById_Unit_Success() throws Exception {
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        mockMvc.perform(get("/api/session/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void testFindById_SessionNotFound() {
        when(sessionService.getById(anyLong())).thenReturn(null);

        ResponseEntity<?> response = sessionController.findById("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    public void testFindById_NumberFormatException() {
        ResponseEntity<?> response = sessionController.findById("notANumber");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser
    public void testFindAll_Unit() throws Exception {
        List<Session> sessions = new ArrayList<>();
        sessions.add(session);

        Session session2 = new Session();
        session2.setId(2L);
        sessions.add(session2);

        List<SessionDto> sessionDtos = sessions.stream()
            .map(session -> {
                SessionDto dto = new SessionDto(); 
                dto.setId(session.getId());
                return dto;
            }).collect(Collectors.toList());

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

        mockMvc.perform(get("/api/session"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[1].id", is(2)));

        verify(sessionService, times(1)).findAll();
        verify(sessionMapper, times(1)).toDto(sessions);
    }

    @Test
    @WithMockUser
    public void testCreate_Unit() throws Exception {
        initializeSession();
        initializeDto();

        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        mockMvc.perform(post("/api/session")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(sessionDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)));

        verify(sessionService, times(1)).create(any(Session.class));
        verify(sessionMapper, times(1)).toDto(any(Session.class));
    }

    @Test
    @WithMockUser
    public void testUpdate_Unit_Success() throws Exception {
        initializeSession();
        initializeDto();

        when(sessionService.update(anyLong(), any(Session.class))).thenReturn(session);
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionMapper.toDto(any(Session.class))).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.update("1", sessionDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof SessionDto);
        assertEquals(sessionDto, response.getBody());

        verify(sessionService, times(1)).update(anyLong(), any(Session.class));
        verify(sessionMapper, times(1)).toEntity(any(SessionDto.class));
        verify(sessionMapper, times(1)).toDto(any(Session.class));
    }

    @Test
    public void testUpdate_SessionBadRequest() {
        String id = "notANumber";
        SessionDto sessionDto = new SessionDto();

        ResponseEntity<?> response = sessionController.update(id, sessionDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDelete_Unit_Success() {
        String id = "1";
        Session session = new Session();
        when(sessionService.getById(Long.valueOf(id))).thenReturn(session);

        ResponseEntity<?> response = sessionController.save(id);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        verify(sessionService, times(1)).delete(Long.valueOf(id));
    }

    @Test
    public void testSave_SessionNotFound() {
        when(sessionService.getById(anyLong())).thenReturn(null);

        ResponseEntity<?> response = sessionController.save("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    public void testSave_InvalidId() {
        ResponseEntity<?> response = sessionController.save("invalid");
    
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testParticipate() {
        String sessionId = "1";
        String userId = "2";
        doNothing().when(sessionService).participate(Long.valueOf(sessionId), Long.valueOf(userId));

        ResponseEntity<?> response = sessionController.participate(sessionId, userId);

        verify(sessionService, times(1)).participate(Long.valueOf(sessionId), Long.valueOf(userId));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void testParticipate_BadRequest() {
        String id = "notANumber";
        String userId = "1";

        SessionController controller = new SessionController(sessionService, sessionMapper);

        doThrow(NumberFormatException.class).when(sessionService).participate(anyLong(), anyLong());

        ResponseEntity<?> response = controller.participate(id, userId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testNoLongerParticipate() {
        String sessionId = "1";
        String userId = "2";
        doNothing().when(sessionService).noLongerParticipate(Long.valueOf(sessionId), Long.valueOf(userId));
    
        ResponseEntity<?> response = sessionController.noLongerParticipate(sessionId, userId);
        verify(sessionService, times(1)).noLongerParticipate(Long.valueOf(sessionId), Long.valueOf(userId));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void testNoLongerParticipate_BadRequest() {
        String sessionId = "notANumber";
        String userId = "123";
    
        doThrow(NumberFormatException.class).when(sessionService).noLongerParticipate(anyLong(), anyLong());
    
        ResponseEntity<?> response = sessionController.noLongerParticipate(sessionId, userId); 
    
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
