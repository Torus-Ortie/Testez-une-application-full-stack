package com.openclassrooms.starterjwt.controllers;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;

@SpringBootTest
@AutoConfigureMockMvc
public class TeacherControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private TeacherMapper teacherMapper;
    private TeacherDto teacherDto;

    @BeforeEach
    public void setup() {
        teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setFirstName("Margot");
        teacherDto.setLastName("DELAHAYE");
    
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Margot");
        teacher.setLastName("DELAHAYE");

        List<TeacherDto> teacherDtos = Arrays.asList(teacherDto);

        List<Teacher> teachers = Arrays.asList(teacher);

        when(teacherService.findById(1L)).thenReturn(teacher);
        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);
    }
    
    @Test
    @WithMockUser
    public void testFindById() throws Exception {
        mockMvc.perform(get("/api/teacher/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("Margot")))
            .andExpect(jsonPath("$.lastName", is("DELAHAYE")));

        verify(teacherService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser
    public void testFindById_BadRequest() throws Exception {
        mockMvc.perform(get("/api/teacher/abc"))
            .andExpect(status().isBadRequest());

        verify(teacherService, times(0)).findById(anyLong());
    }
    
    @Test
    @WithMockUser
    public void testGetAllTeachers() throws Exception {
        mockMvc.perform(get("/api/teacher"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].firstName", is("Margot")))
            .andExpect(jsonPath("$[0].lastName", is("DELAHAYE")));

        verify(teacherService, times(1)).findAll();
    }
}
