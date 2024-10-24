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

    // Initialise une session pour les tests
    private void initializeSession() {
        session.setName("Test Session"); // Définit le nom de la session
        session.setDate(new Date()); // Définit la date de la session
        session.setTeacher(new Teacher(1L, "DELAHAYE", "Margot", null, null)); // Définit l'enseignant de la session
        session.setDescription("This is a test session."); // Définit la description de la session
        session.setUsers(Arrays.asList(
            new User(2L, "user1@studio.com", "Studio", "User1", "test!1234", false, null, null), // Ajoute un utilisateur à la session
            new User(3L, "user2@studio.com", "Studio", "User2", "test!12345", false, null, null) // Ajoute un autre utilisateur à la session
        ));
    }

    // Initialise un DTO de session pour les tests
    private void initializeDto() {
        sessionDto.setName("Test Session"); // Définit le nom du DTO de la session
        sessionDto.setDate(new Date()); // Définit la date du DTO de la session
        sessionDto.setTeacher_id(1L); // Définit l'ID de l'enseignant du DTO de la session
        sessionDto.setDescription("Test Description"); // Définit la description du DTO de la session
        sessionDto.setUsers(Arrays.asList(1L, 2L)); // Définit les utilisateurs du DTO de la session
        sessionDto.setCreatedAt(LocalDateTime.now()); // Définit la date de création du DTO de la session
        sessionDto.setUpdatedAt(LocalDateTime.now()); // Définit la date de mise à jour du DTO de la session
    }

    @Test // Indique que c'est une méthode de test
    @WithMockUser // Exécute le test avec un utilisateur mocké
    public void testFindById_Unit_Success() throws Exception {
        // Arrange
        when(sessionService.getById(1L)).thenReturn(session); // Mocke le comportement de sessionService.getById
        when(sessionMapper.toDto(session)).thenReturn(sessionDto); // Mocke le comportement de sessionMapper.toDto
        // Act
        mockMvc.perform(get("/api/session/{id}", 1L)) // Effectue une requête GET sur /api/session/{id}
        // Assert
                .andExpect(status().isOk()) // Attend un statut 200 OK
               .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Attend un contenu de type application/json
               .andExpect(jsonPath("$.id", is(1))); // Attend que l'ID de la réponse soit 1
    }

    @Test // Indique que c'est une méthode de test
    public void testFindById_SessionNotFound() {
        // Arrange
        when(sessionService.getById(anyLong())).thenReturn(null); // Simuler le comportement de sessionService.getById pour qu'il renvoie null, ce qui signifie que la session n'existe pas
        // Act
        ResponseEntity<?> response = sessionController.findById("1"); // Appeler la méthode findById avec un ID valide
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); // Vérifier que le statut de la réponse est NOT_FOUND
    }
    
    @Test // Indique que c'est une méthode de test
    public void testFindById_NumberFormatException() {
        // Arrange // Pas de configuration nécessaire pour ce test
        // Act
        ResponseEntity<?> response = sessionController.findById("notANumber"); // Appeler la méthode findById avec un ID invalide (qui n'est pas un nombre)
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Vérifier que le statut de la réponse est BAD_REQUEST
    }

    @Test // Indique que c'est une méthode de test
    @WithMockUser // Exécute le test avec un utilisateur mocké
    public void testFindAll_Unit() throws Exception {
        // Arrange
        List<Session> sessions = new ArrayList<>(); // Crée une liste de sessions
        sessions.add(session); // Ajoute la session à la liste

        Session session2 = new Session(); // Crée une deuxième session
        session2.setId(2L); // Définit l'ID de la deuxième session
        sessions.add(session2); // Ajoute la deuxième session à la liste

        List<SessionDto> sessionDtos = sessions.stream() // Convertit la liste de sessions en une liste de SessionDto
            .map(session -> {
                SessionDto dto = new SessionDto(); // Crée un nouveau SessionDto
                dto.setId(session.getId()); // Définit l'ID du SessionDto
                return dto;
            }).collect(Collectors.toList());

        when(sessionService.findAll()).thenReturn(sessions); // Mocke le comportement de sessionService.findAll
        when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos); // Mocke le comportement de sessionMapper.toDto
        // Act & Assert
        mockMvc.perform(get("/api/session")) // Effectue une requête GET sur /api/session
            .andExpect(status().isOk()) // Attend un statut 200 OK
            .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Attend un contenu de type application/json
            .andExpect(jsonPath("$", hasSize(2))) // Attend que la réponse contienne 2 éléments
            .andExpect(jsonPath("$[0].id", is(1))) // Attend que l'ID du premier élément soit 1
            .andExpect(jsonPath("$[1].id", is(2))); // Attend que l'ID du deuxième élément soit 2

        verify(sessionService, times(1)).findAll(); // Vérifie que sessionService.findAll a été appelé une fois
        verify(sessionMapper, times(1)).toDto(sessions); // Vérifie que sessionMapper.toDto a été appelé une fois
    }

    @Test // Teste la création d'une session
    @WithMockUser
    public void testCreate_Unit() throws Exception {
        initializeSession(); // Initialise la session
        initializeDto(); // Initialise le DTO de la session
        // Arrange
        // Mocke le comportement des méthodes toEntity, create et toDto
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);
        // Act
        // Effectue une requête POST et vérifie la réponse
        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)));
        // Assert
        // Vérifie que les méthodes create et toDto ont été appelées une fois
        verify(sessionService, times(1)).create(any(Session.class));
        verify(sessionMapper, times(1)).toDto(any(Session.class));
    }

    @Test // Teste la mise à jour d'une session avec succès
    @WithMockUser
    public void testUpdate_Unit_Success() throws Exception {
        // Arrange
        initializeSession(); // Initialise la session
        initializeDto(); // Initialise le DTO de la session

        // Mocke le comportement des méthodes update, toEntity et toDto
        when(sessionService.update(anyLong(), any(Session.class))).thenReturn(session);
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionMapper.toDto(any(Session.class))).thenReturn(sessionDto);
        // Act
        // Effectue une mise à jour et vérifie la réponse
        ResponseEntity<?> response = sessionController.update("1", sessionDto);
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Vérifie le statut de la réponse
        assertTrue(response.getBody() instanceof SessionDto); // Vérifie le type du corps de la réponse
        assertEquals(sessionDto, response.getBody()); // Vérifie le contenu du corps de la réponse

        // Vérifie que les méthodes update, toEntity et toDto ont été appelées une fois
        verify(sessionService, times(1)).update(anyLong(), any(Session.class));
        verify(sessionMapper, times(1)).toEntity(any(SessionDto.class));
        verify(sessionMapper, times(1)).toDto(any(Session.class));
    }

    @Test // Annoter la méthode avec @Test pour indiquer qu'il s'agit d'un test unitaire
    public void testUpdate_SessionBadRequest() {
        // Arrange
        String id = "notANumber"; // Définir un ID de session non numérique pour déclencher NumberFormatException
        SessionDto sessionDto = new SessionDto(); // Créer un nouvel objet SessionDto
        // Act
        ResponseEntity<?> response = sessionController.update(id, sessionDto); // Appeler la méthode update sur le contrôleur avec l'ID de la session et l'objet sessionDto
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Vérifier que le statut de la réponse est 400 BAD_REQUEST
    }

    // Teste la suppression d'une session
    @Test
    public void testDelete_Unit_Success() {
        // Arrange
        String id = "1"; // Définir l'ID de la session à supprimer
        Session session = new Session(); // Créer une nouvelle session
        when(sessionService.getById(Long.valueOf(id))).thenReturn(session); // Simuler le comportement de sessionService pour retourner la session lorsqu'on cherche la session
        // Act
        ResponseEntity<?> response = sessionController.save(id); // Appeler la méthode de suppression sur le contrôleur avec l'ID de la session
        // Assert
        assertThat(response.getStatusCode(), is(HttpStatus.OK)); // Vérifier que le statut de la réponse est 200 OK
        verify(sessionService, times(1)).delete(Long.valueOf(id)); // Vérifier que la méthode delete de sessionService a été appelée une fois avec l'ID de la session
    }

    @Test
    public void testSave_SessionNotFound() {
        // Arrange
        // Simuler le comportement de sessionService.getById pour qu'il renvoie null, ce qui signifie que la session n'existe pas
        when(sessionService.getById(anyLong())).thenReturn(null);
    
        // Act
        // Appeler la méthode save avec un ID valide
        ResponseEntity<?> response = sessionController.save("1");
    
        // Assert
        // Vérifier que le statut de la réponse est NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    public void testSave_InvalidId() {
        // Arrange
        // Pas de configuration nécessaire pour ce test
    
        // Act
        // Appeler la méthode save avec un ID invalide (qui n'est pas un nombre)
        ResponseEntity<?> response = sessionController.save("invalid");
    
        // Assert
        // Vérifier que le statut de la réponse est BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Teste la participation d'un utilisateur à une session
    @Test
    public void testParticipate() {
        // Arrange
        String sessionId = "1"; // Définir l'ID de la session
        String userId = "2"; // Définir l'ID de l'utilisateur
        doNothing().when(sessionService).participate(Long.valueOf(sessionId), Long.valueOf(userId)); // Simuler le comportement de sessionService pour ne rien faire lorsqu'on appelle la méthode participate

        // Act
        ResponseEntity<?> response = sessionController.participate(sessionId, userId); // Appeler la méthode de participation sur le contrôleur avec l'ID de la session et l'ID de l'utilisateur

        // Assert
        verify(sessionService, times(1)).participate(Long.valueOf(sessionId), Long.valueOf(userId)); // Vérifier que la méthode participate du service SessionService a été appelée une fois avec les mêmes ID de session et d'utilisateur
        assertThat(response.getStatusCode(), is(HttpStatus.OK)); // Vérifier que le statut de la réponse est 200 OK
    }

    @Test
    public void testParticipate_BadRequest() {
        // Arrange
        // On définit des valeurs pour id et userId qui ne sont pas des nombres valides
        String id = "notANumber";
        String userId = "1";

        // On crée une instance de SessionController avec les services mockés
        SessionController controller = new SessionController(sessionService, sessionMapper);

        // On configure le service mocké pour lever une NumberFormatException
        // quand la méthode participate est appelée avec n'importe quel long
        doThrow(NumberFormatException.class).when(sessionService).participate(anyLong(), anyLong());

        // Act
        // On appelle la méthode participate du contrôleur avec les valeurs définies plus haut
        ResponseEntity<?> response = controller.participate(id, userId);

        // Assert
        // On vérifie que le statut de la réponse est BAD_REQUEST
        // Ce qui signifie que le contrôleur a bien géré l'exception et renvoyé la bonne réponse
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    // Teste le renoncement de participation d'un utilisateur à une session
    @Test
    public void testNoLongerParticipate() {
        // Arrange
        String sessionId = "1"; // Définir l'ID de la session
        String userId = "2"; // Définir l'ID de l'utilisateur
        doNothing().when(sessionService).noLongerParticipate(Long.valueOf(sessionId), Long.valueOf(userId)); // Simuler le comportement de sessionService pour ne rien faire lorsqu'on appelle la méthode noLongerParticipate
    
        // Act
        ResponseEntity<?> response = sessionController.noLongerParticipate(sessionId, userId); // Appeler la méthode noLongerParticipate sur le contrôleur avec l'ID de la session et l'ID de l'utilisateur
    
        // Assert
        verify(sessionService, times(1)).noLongerParticipate(Long.valueOf(sessionId), Long.valueOf(userId)); // Vérifier que la méthode noLongerParticipate du service SessionService a été appelée une fois avec les mêmes ID de session et d'utilisateur
        assertThat(response.getStatusCode(), is(HttpStatus.OK)); // Vérifier que le statut de la réponse est 200 OK
    }

    @Test
    public void testNoLongerParticipate_BadRequest() {
        // Arrange
        String sessionId = "notANumber"; // Définir un ID de session non numérique pour déclencher NumberFormatException
        String userId = "123"; // Définir un ID d'utilisateur quelconque
    
        // Simuler le comportement de sessionService pour lancer une NumberFormatException lorsqu'on appelle la méthode noLongerParticipate
        doThrow(NumberFormatException.class).when(sessionService).noLongerParticipate(anyLong(), anyLong());
    
        // Act
        ResponseEntity<?> response = sessionController.noLongerParticipate(sessionId, userId); // Appeler la méthode noLongerParticipate sur le contrôleur avec l'ID de la session et l'ID de l'utilisateur
    
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Vérifier que le statut de la réponse est 400 BAD_REQUEST
    }

}
