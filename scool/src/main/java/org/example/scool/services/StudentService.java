package org.example.scool.services;

import org.example.scool.dtos.StudentDTO;
import org.example.scool.exceptions.StudentCreationException;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    StudentDTO createStudent(StudentDTO studentDTO) throws StudentCreationException;
    StudentDTO updateStudent(Long id, StudentDTO studentDTO);
    void deleteStudent(Long id);
    Optional<StudentDTO> getStudentById(Long id);
    List<StudentDTO> getAllStudents();
    List<StudentDTO> searchStudentsByLastName(String lastName);
    long getTotalStudentCount();
    StudentDTO enrollInModule(Long studentId, Long moduleId);
}