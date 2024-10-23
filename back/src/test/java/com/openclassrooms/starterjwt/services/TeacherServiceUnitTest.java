package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.openclassrooms.starterjwt.utils.TestConstant.TEST_TEACHER_FIRST_NAME;
import static com.openclassrooms.starterjwt.utils.TestConstant.TEST_TEACHER_LAST_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    private static final Long MOCK_TEACHER_ID = 1L;

    private static final Long NEW_TEACHER_ID = 2L;
    private static final Long NON_EXISTING_TEACHER_ID = 3L;

    @InjectMocks
    private TeacherService teacherService;

    @Mock
    private TeacherRepository teacherRepository;

    private Teacher mockTeacher;

    @BeforeEach
    void setUp() {
        mockTeacher = createTeacher(MOCK_TEACHER_ID);
    }

    private Teacher createTeacher(Long id) {
        return new Teacher(
                id,
                TEST_TEACHER_LAST_NAME,
                TEST_TEACHER_FIRST_NAME,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Find all teachers")
    void shouldReturnAllTeachersWhenFindAllIsCalled() {
        // Arrange
        Teacher secondTeacher = createTeacher(NEW_TEACHER_ID);
        List<Teacher> expectedTeachers = Arrays.asList(mockTeacher, secondTeacher);
        when(teacherRepository.findAll()).thenReturn(expectedTeachers);

        // Act
        List<Teacher> actualTeachers = teacherService.findAll();

        // Assert
        assertEquals(2, actualTeachers.size());
        assertEquals(mockTeacher, actualTeachers.get(0));
    }

    @Test
    @DisplayName("Find Teacher By Existing ID")
    void shouldReturnTeacherWhenFindByIdIsCalledWithExistingId() {
        // Arrange
        when(teacherRepository.findById(MOCK_TEACHER_ID)).thenReturn(Optional.of(mockTeacher));

        // Act
        Teacher actualTeacher = teacherService.findById(MOCK_TEACHER_ID);

        // Assert
        assertEquals(mockTeacher, actualTeacher);
    }

    @Test
    @DisplayName("Find Teacher By Non-Existing ID")
    void shouldReturnNullWhenFindByIdIsCalledWithNonExistingId() {
        // Arrange
        when(teacherRepository.findById(NON_EXISTING_TEACHER_ID)).thenReturn(Optional.empty());

        // Act
        Teacher actualTeacher = teacherService.findById(NON_EXISTING_TEACHER_ID);

        // Assert
        assertNull(actualTeacher);
    }
}