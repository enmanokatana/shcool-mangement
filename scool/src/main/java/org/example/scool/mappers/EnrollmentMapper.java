package org.example.scool.mappers;
import org.example.scool.dtos.EnrollmentDTO;
import org.example.scool.dtos.ModuleDTO;
import org.example.scool.dtos.StudentDTO;
import org.example.scool.models.Enrollment;
import org.example.scool.models.Module;
import org.example.scool.models.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {StudentMapper.class, ModuleMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EnrollmentMapper {

    @Mapping(target = "student", qualifiedByName = "mapStudent")
    @Mapping(target = "module", qualifiedByName = "mapModule")
    EnrollmentDTO toDTO(Enrollment enrollment);

    Enrollment toEntity(EnrollmentDTO enrollmentDTO);

    @Named("mapStudent")
    default StudentDTO mapStudent(Student student) {
        if (student == null) return null;

        // Ensure enrollments or other collections are detached from the session before mapping
        StudentDTO dto = StudentDTO.builder()
                .id(student.getId())
                .studentNumber(student.getStudentNumber())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .dateOfBirth(student.getDateOfBirth())
                .enrollmentDate(student.getEnrollmentDate())
                .build();

        // Safely copy any collections if needed
        if (student.getEnrollments() != null) {
            dto.setEnrollments(
                    student.getEnrollments().stream()
                            .map(this::mapEnrollmentForStudent)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    @Named("mapModule")
    default ModuleDTO mapModule(Module module) {
        if (module == null) return null;

        return new ModuleDTO(
                module.getId(),
                module.getModuleCode(),
                module.getModuleName(),
                module.getDescription(),
                null,
                null
        );
    }

    // Example method to map a simplified enrollment for the student DTO (if necessary)
    default EnrollmentDTO mapEnrollmentForStudent(Enrollment enrollment) {
        return EnrollmentDTO.builder()
                .id(enrollment.getId())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus())
                .build();
    }
}
