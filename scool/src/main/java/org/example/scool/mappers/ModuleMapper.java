package org.example.scool.mappers;


import org.example.scool.dtos.EnrollmentDTO;
import org.example.scool.dtos.ModuleDTO;
import org.example.scool.dtos.ProfessorDTO;
import org.example.scool.dtos.StudentDTO;
import org.example.scool.models.Enrollment;
import org.example.scool.models.Professor;
import org.example.scool.models.Module;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ModuleMapper {
    @Mapping(target = "professor", qualifiedByName = "mapProfessor")
    @Mapping(target = "enrollments", qualifiedByName = "mapEnrollments")
    ModuleDTO toDTO(Module module);

    Module toEntity(ModuleDTO moduleDTO);

    @Named("mapProfessor")
    default ProfessorDTO mapProfessor(Professor professor) {
        return professor == null ? null :
                new ProfessorDTO(
                        professor.getId(),
                        professor.getEmployeeNumber(),
                        professor.getFirstName(),
                        professor.getLastName(),
                        professor.getEmail(),
                        professor.getDepartment(),
                        null
                );
    }

    @Named("mapEnrollments")
    default List<EnrollmentDTO> mapEnrollments(Set<Enrollment> enrollments) {
        return enrollments == null ? null :
                enrollments.stream()
                        .map(e -> new EnrollmentDTO(
                                e.getId(),
                                new StudentDTO(e.getStudent().getId(), null, null, null, null, null, null, null),
                                null,
                                e.getEnrollmentDate(),
                                e.getStatus()
                        ))
                        .collect(Collectors.toList());
    }
}