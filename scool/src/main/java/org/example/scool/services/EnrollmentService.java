package org.example.scool.services;

import org.example.scool.dtos.EnrollmentDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EnrollmentService {
    EnrollmentDTO createEnrollment(EnrollmentDTO enrollmentDTO);
    void cancelEnrollment(Long enrollmentId);

    @Transactional(readOnly = true)
    List<EnrollmentDTO> getAllEnrollments();

    @Transactional(readOnly = true)
    List<EnrollmentDTO> getEnrollmentsByStudentAndModule(Long studentId, Long moduleId);

    List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId);
    List<EnrollmentDTO> getEnrollmentsByModule(Long moduleId);
    long countActiveEnrollments();

    Optional<EnrollmentDTO> getEnrollmentById(Long id);

    void updateEnrollment(Long id, EnrollmentDTO enrollmentDTO);
}