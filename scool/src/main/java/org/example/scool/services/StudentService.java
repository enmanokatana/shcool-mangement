package org.example.scool.services;

import org.example.scool.dtos.StudentDTO;
import org.example.scool.exceptions.DataDeleteException;
import org.example.scool.exceptions.StudentCreationException;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

public interface StudentService {
    List<StudentDTO> getAllStudents();

    StudentDTO createStudent(StudentDTO studentDTO) throws StudentCreationException;

    StudentDTO updateStudent(Long id, StudentDTO studentDTO);

    void deleteStudent(Long id) throws DataDeleteException;

    Optional<StudentDTO> getStudentById(Long id);

    List<StudentDTO> searchStudentsByLastName(String lastName);

    long getTotalStudentCount();

    StudentDTO enrollInModule(Long studentId, Long moduleId);

    // New methods
    StudentDTO updateProfilePhoto(Long id, MultipartFile photo);

    StudentDTO updateStudentStatus(Long id, String status);

    List<StudentDTO> getStudentsByStatus(String status);

    StudentDTO updateEmergencyContact(Long id, String contactName, String contactPhone);

    List<StudentDTO> getActiveStudents();

    StudentDTO updateStudentProfile(Long id, StudentDTO studentDTO);

    void validateStudentData(StudentDTO studentDTO);

    boolean isEmailUnique(String email, Long excludeId);

    boolean isStudentNumberUnique(String studentNumber, Long excludeId);
}