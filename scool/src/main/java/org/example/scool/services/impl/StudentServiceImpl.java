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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    private final String UPLOAD_DIR = "uploads/profile-photos/";

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
            validateStudentData(studentDTO);
            Student student = studentMapper.toEntity(studentDTO);
            student.setEnrollmentDate(LocalDate.now());
            student.setStatus(Student.StudentStatus.ACTIVE);
            Student savedStudent = studentRepository.save(student);
            return studentMapper.toDTO(savedStudent);
        } catch (DuplicateEntityException ex) {
            log.error("Duplicate entity error: {}", ex.getMessage());
            throw ex;
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

            validateStudentData(studentDTO);

            // Update basic fields
            existingStudent.setFirstName(studentDTO.getFirstName());
            existingStudent.setLastName(studentDTO.getLastName());
            existingStudent.setEmail(studentDTO.getEmail());
            existingStudent.setDateOfBirth(studentDTO.getDateOfBirth());
            existingStudent.setPhoneNumber(studentDTO.getPhoneNumber());
            existingStudent.setAddress(studentDTO.getAddress());

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
    public void deleteStudent(Long id) throws DataDeleteException {
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
            studentRepository.delete(student);
            log.info("Student with id {} deleted successfully", id);
        } catch (EntityNotFoundException ex) {
            log.error("Student not found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error deleting student: {}", ex.getMessage());
            throw new DataDeleteException("Failed to delete student", ex);
        }
    }

    @Override
    public Optional<StudentDTO> getStudentById(Long id) {
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
            return Optional.of(studentMapper.toDTO(student));
        } catch (EntityNotFoundException ex) {
            log.error("Student not found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error fetching student by id: {}", ex.getMessage());
            throw new DataFetchException("Failed to fetch student by id", ex);
        }
    }

    @Override
    public List<StudentDTO> searchStudentsByLastName(String lastName) {
        try {
            List<Student> students = studentRepository.findByLastNameContainingIgnoreCase(lastName);
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
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));
            Module module = moduleRepository.findById(moduleId)
                    .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + moduleId));

            if (enrollmentRepository.existsByStudentAndModule(student, module)) {
                throw new DuplicateEntityException("Student is already enrolled in this module");
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setModule(module);
            enrollment.setEnrollmentDate(LocalDate.now());

            enrollmentRepository.save(enrollment);

            log.info("Student {} successfully enrolled in module {}", studentId, moduleId);
            return studentMapper.toDTO(student);
        } catch (EntityNotFoundException | DuplicateEntityException ex) {
            log.error("Error enrolling student in module: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during module enrollment: {}", ex.getMessage());
            throw new DataUpdateException("Failed to enroll student in module", ex);
        }
    }

    @Override
    public StudentDTO updateProfilePhoto(Long id, MultipartFile photo) {
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));

            if (photo.isEmpty()) {
                throw new ProfilePhotoException("Please select a photo to upload");
            }

            // Validate file type
            String fileExtension = StringUtils.getFilenameExtension(photo.getOriginalFilename());
            if (!isValidImageExtension(fileExtension)) {
                throw new ProfilePhotoException("Invalid file type. Please upload a JPG, PNG or GIF file");
            }

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String filename = UUID.randomUUID().toString() + "." + fileExtension;
            Path filePath = uploadPath.resolve(filename);

            // Save file
            Files.copy(photo.getInputStream(), filePath);

            // Update student profile photo path
            student.setProfilePicture(filename);
            Student updatedStudent = studentRepository.save(student);

            return studentMapper.toDTO(updatedStudent);
        } catch (IOException ex) {
            log.error("Error saving profile photo: {}", ex.getMessage());
            throw new ProfilePhotoException("Failed to save profile photo", ex);
        }
    }

    @Override
    public StudentDTO updateStudentStatus(Long id, String status) {
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));

            Student.StudentStatus newStatus = Student.StudentStatus.valueOf(status.toUpperCase());

            // Add validation logic for status transitions if needed
            student.setStatus(newStatus);

            Student updatedStudent = studentRepository.save(student);
            return studentMapper.toDTO(updatedStudent);
        } catch (IllegalArgumentException ex) {
            throw new InvalidStatusTransitionException("Invalid status: " + status);
        }
    }

    @Override
    public List<StudentDTO> getStudentsByStatus(String status) {
        try {
            Student.StudentStatus studentStatus = Student.StudentStatus.valueOf(status.toUpperCase());
            List<Student> students = studentRepository.findByStatus(studentStatus);
            return students.stream()
                    .map(studentMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException ex) {
            throw new InvalidStatusTransitionException("Invalid status: " + status);
        }
    }

    @Override
    public StudentDTO updateEmergencyContact(Long id, String contactName, String contactPhone) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));

        student.setEmergencyContact(contactName);
        student.setEmergencyPhone(contactPhone);

        Student updatedStudent = studentRepository.save(student);
        return studentMapper.toDTO(updatedStudent);
    }

    @Override
    public List<StudentDTO> getActiveStudents() {
        List<Student> activeStudents = studentRepository.findByStatus(Student.StudentStatus.ACTIVE);
        return activeStudents.stream()
                .map(studentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDTO updateStudentProfile(Long id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));

        // Update all profile fields
        student.setPhoneNumber(studentDTO.getPhoneNumber());
        student.setAddress(studentDTO.getAddress());
        student.setEmergencyContact(studentDTO.getEmergencyContact());
        student.setEmergencyPhone(studentDTO.getEmergencyPhone());
        student.setGender(studentDTO.getGender());

        Student updatedStudent = studentRepository.save(student);
        return studentMapper.toDTO(updatedStudent);
    }

    // Existing methods remain the same...

    @Override
    public void validateStudentData(StudentDTO studentDTO) {
        if (!isEmailUnique(studentDTO.getEmail(), studentDTO.getId())) {
            throw new DuplicateEntityException("Email already exists: " + studentDTO.getEmail());
        }

        if (!isStudentNumberUnique(studentDTO.getStudentNumber(), studentDTO.getId())) {
            throw new DuplicateEntityException("Student number already exists: " + studentDTO.getStudentNumber());
        }
    }

    @Override
    public boolean isEmailUnique(String email, Long excludeId) {
        return studentRepository.findByEmailAndIdNot(email, excludeId).isEmpty();
    }

    @Override
    public boolean isStudentNumberUnique(String studentNumber, Long excludeId) {
        return studentRepository.findByStudentNumberAndIdNot(studentNumber, excludeId).isEmpty();
    }

    private boolean isValidImageExtension(String extension) {
        if (extension == null) {
            return false;
        }
        return extension.toLowerCase().matches("jpg|jpeg|png|gif");
    }
}