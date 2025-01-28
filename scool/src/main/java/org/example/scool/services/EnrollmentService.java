package org.example.scool.services;

import org.example.scool.dtos.EnrollmentDTO;

import java.util.List;

public interface EnrollmentService {
    EnrollmentDTO createEnrollment(EnrollmentDTO enrollmentDTO);
    void cancelEnrollment(Long enrollmentId);
    List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId);
    List<EnrollmentDTO> getEnrollmentsByModule(Long moduleId);
    long countActiveEnrollments();
}