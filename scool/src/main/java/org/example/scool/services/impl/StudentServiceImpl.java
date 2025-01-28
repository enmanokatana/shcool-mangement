package org.example.scool.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scool.dtos.StudentDTO;
import org.example.scool.exceptions.*;
import org.example.scool.mappers.StudentMapper;
import org.example.scool.models.Enrollment;
import org.example.scool.models.Student;
import org.example.scool.models.Module;
import org.example.scool.repositories.EnrollmentRepository;
import org.example.scool.repositories.ModuleRepository;
import org.example.scool.repositories.StudentRepository;
import org.example.scool.services.StudentService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final ModuleRepository moduleRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentMapper studentMapper;

    @Override
    public List<StudentDTO> getAllStudents() {
        try {
            List<Student> students = studentRepository.findAll();
            return students.stream()
                    .map(studentMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error fetching all students: {}", ex.getMessage());
            throw new DataFetchException("Failed to fetch students", ex);
        }
    }

    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) throws StudentCreationException {
        try {
            validateUniqueStudentConstraints(studentDTO); // Validate unique constraints
            Student student = studentMapper.toEntity(studentDTO);
            Student savedStudent = studentRepository.save(student);
            return studentMapper.toDTO(savedStudent);
        } catch (DuplicateEntityException ex) {
            log.error("Duplicate entity error: {}", ex.getMessage());
            throw ex; // Re-throw the exception
        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation: {}", ex.getMessage());
            throw new StudentCreationException("Unable to create student due to data integrity violation", ex);
        } catch (Exception ex) {
            log.error("Error creating student: {}", ex.getMessage());
            throw new StudentCreationException("Failed to create student", ex);
        }
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        try {
            Student existingStudent = studentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));

            // Update fields
            existingStudent.setStudentNumber(studentDTO.getStudentNumber());
            existingStudent.setFirstName(studentDTO.getFirstName());
            existingStudent.setLastName(studentDTO.getLastName());
            existingStudent.setEmail(studentDTO.getEmail());
            existingStudent.setDateOfBirth(studentDTO.getDateOfBirth());

            Student updatedStudent = studentRepository.save(existingStudent);
            return studentMapper.toDTO(updatedStudent);
        } catch (EntityNotFoundException ex) {
            log.error("Student not found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating student: {}", ex.getMessage());
            throw new DataUpdateException("Failed to update student", ex);
        }
    }

    @Override
    public void deleteStudent(Long id) {
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));

            // Check if the student has active enrollments
            boolean hasActiveEnrollments = student.getEnrollments().stream()
                    .anyMatch(e -> e.getStatus() == Enrollment.EnrollmentStatus.ACTIVE);

            if (hasActiveEnrollments) {
                throw new IllegalOperationException("Cannot delete student with active enrollments");
            }

            studentRepository.delete(student);
        } catch (EntityNotFoundException ex) {
            log.error("Student not found: {}", ex.getMessage());
            throw ex;
        } catch (IllegalOperationException ex) {
            log.error("Illegal operation: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error deleting student: {}", ex.getMessage());
            throw new DataDeletionException("Failed to delete student", ex);
        }
    }

    @Override
    public Optional<StudentDTO> getStudentById(Long id) {
        try {
            Optional<Student> student = studentRepository.findById(id);
            return student.map(studentMapper::toDTO);
        } catch (Exception ex) {
            log.error("Error fetching student by ID: {}", ex.getMessage());
            throw new DataFetchException("Failed to fetch student by ID", ex);
        }
    }

    @Override
    public List<StudentDTO> searchStudentsByLastName(String lastName) {
        try {
            List<Student> students = studentRepository.findByLastNameContaining(lastName);
            return students.stream()
                    .map(studentMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error searching students by last name: {}", ex.getMessage());
            throw new DataFetchException("Failed to search students by last name", ex);
        }
    }

    @Override
    public long getTotalStudentCount() {
        try {
            return studentRepository.count();
        } catch (Exception ex) {
            log.error("Error fetching total student count: {}", ex.getMessage());
            throw new DataFetchException("Failed to fetch total student count", ex);
        }
    }

    @Override
    public StudentDTO enrollInModule(Long studentId, Long moduleId) {
        try {
            // Fetch student and module
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));
            Module module = moduleRepository.findById(moduleId)
                    .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + moduleId));

            // Check if already enrolled
            boolean alreadyEnrolled = student.getEnrollments().stream()
                    .anyMatch(e -> e.getModule().getId().equals(moduleId) &&
                            e.getStatus() == Enrollment.EnrollmentStatus.ACTIVE);

            if (alreadyEnrolled) {
                throw new DuplicateEnrollmentException("Student is already enrolled in this module");
            }

            // Create enrollment
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setModule(module);
            enrollment.setEnrollmentDate(LocalDate.now());
            enrollment.setStatus(Enrollment.EnrollmentStatus.ACTIVE);

            enrollmentRepository.save(enrollment);

            return studentMapper.toDTO(student);
        } catch (EntityNotFoundException ex) {
            log.error("Entity not found: {}", ex.getMessage());
            throw ex;
        } catch (DuplicateEnrollmentException ex) {
            log.error("Duplicate enrollment: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error enrolling student in module: {}", ex.getMessage());
            throw new EnrollmentException("Failed to enroll student in module", ex);
        }
    }

    private void validateUniqueStudentConstraints(StudentDTO studentDTO) {
        studentRepository.findByEmail(studentDTO.getEmail())
                .ifPresent(s -> {
                    throw new DuplicateEntityException("Email already exists: " + studentDTO.getEmail());
                });

        studentRepository.findByStudentNumber(studentDTO.getStudentNumber())
                .ifPresent(s -> {
                    throw new DuplicateEntityException("Student number already exists: " + studentDTO.getStudentNumber());
                });
    }
}