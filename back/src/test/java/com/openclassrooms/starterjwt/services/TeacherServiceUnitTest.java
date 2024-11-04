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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @InjectMocks
    private TeacherService teacherService;

    @Mock
    private TeacherRepository teacherRepository;

    private Teacher mockTeacher;

    @BeforeEach
    void setUp() {
        mockTeacher = createTeacher(1L);
    }

    private Teacher createTeacher(Long id) {
        return new Teacher(
                id,
                "Studio",
                "Yoga",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Find all teachers")
    void shouldReturnAllTeachersWhenFindAllIsCalled() {
        Teacher secondTeacher = createTeacher(2L);
        List<Teacher> expectedTeachers = Arrays.asList(mockTeacher, secondTeacher);
        when(teacherRepository.findAll()).thenReturn(expectedTeachers);

        List<Teacher> actualTeachers = teacherService.findAll();

        assertEquals(2, actualTeachers.size());
        assertEquals(mockTeacher, actualTeachers.get(0));
    }

    @Test
    @DisplayName("Find Teacher By Existing ID")
    void shouldReturnTeacherWhenFindByIdIsCalledWithExistingId() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(mockTeacher));

        Teacher actualTeacher = teacherService.findById(1L);

        assertEquals(mockTeacher, actualTeacher);
    }

    @Test
    @DisplayName("Find Teacher By Non-Existing ID")
    void shouldReturnNullWhenFindByIdIsCalledWithNonExistingId() {
        when(teacherRepository.findById(3L)).thenReturn(Optional.empty());

        Teacher actualTeacher = teacherService.findById(3L);

        assertNull(actualTeacher);
    }
}