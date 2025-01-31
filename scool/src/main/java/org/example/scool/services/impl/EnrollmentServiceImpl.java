package org.example.scool.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scool.dtos.EnrollmentDTO;
import org.example.scool.exceptions.DuplicateEnrollmentException;
import org.example.scool.exceptions.EnrollmentCreationException;
import org.example.scool.mappers.EnrollmentMapper;
import org.example.scool.models.Enrollment;
import org.example.scool.repositories.EnrollmentRepository;
import org.example.scool.repositories.ModuleRepository;
import org.example.scool.repositories.StudentRepository;
import org.example.scool.services.EnrollmentService;
import org.springframework.stereotype.Service;
import org.example.scool.models.Module;
import org.example.scool.models.Student;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final ModuleRepository moduleRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    @Transactional
    public EnrollmentDTO createEnrollment(EnrollmentDTO enrollmentDTO) {
        try {
            Student student = studentRepository.findById(enrollmentDTO.getStudent().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Student not found"));

            Module module = moduleRepository.findById(enrollmentDTO.getModule().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Module not found"));

            // Check for existing active enrollment
            boolean exists = enrollmentRepository.findByStudentAndModule(student, module)
                    .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.ACTIVE)
                    .isPresent();

            if (exists) {
                throw new DuplicateEnrollmentException("Student is already enrolled in this module");
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setModule(module);
            enrollment.setEnrollmentDate(LocalDate.now());
            enrollment.setStatus(Enrollment.EnrollmentStatus.ACTIVE);

            Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
            return enrollmentMapper.toDTO(savedEnrollment);
        } catch (Exception ex) {
            log.error("Error creating enrollment: {}", ex.getMessage());
            throw new EnrollmentCreationException("Unable to create enrollment", ex);
        }
    }

    @Override
    public void cancelEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found"));

        enrollment.setStatus(Enrollment.EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EnrollmentDTO> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        return enrollments.stream()
                .map(enrollmentMapper::toDTO)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    @Override
    public List<EnrollmentDTO> getEnrollmentsByStudentAndModule(Long studentId, Long moduleId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndModuleId(studentId, moduleId);
        return enrollments.stream()
                .map(enrollmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        return enrollments.stream()
                .map(enrollmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByModule(Long moduleId) {
        List<Enrollment> enrollments = enrollmentRepository.findByModuleId(moduleId);
        return enrollments.stream()
                .map(enrollmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long countActiveEnrollments() {
        return 0;
    }

    @Override
    public Optional<EnrollmentDTO> getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .map(enrollmentMapper::toDTO);
    }

    @Override
    public void updateEnrollment(Long id, EnrollmentDTO enrollmentDTO) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with id: " + id));

        enrollment.setStatus(enrollmentDTO.getStatus());
        enrollmentRepository.save(enrollment);
    }

}