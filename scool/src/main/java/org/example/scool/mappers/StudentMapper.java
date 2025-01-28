package org.example.scool.mappers;

import org.example.scool.dtos.EnrollmentDTO;
import org.example.scool.dtos.ModuleDTO;
import org.example.scool.dtos.StudentDTO;
import org.example.scool.models.Enrollment;
import org.example.scool.models.Student;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentMapper {
    @Mapping(target = "enrollments", qualifiedByName = "mapEnrollments")
    StudentDTO toDTO(Student student);
    Student toEntity(StudentDTO studentDTO);

    void updateEntityFromDTO(StudentDTO dto, @MappingTarget Student entity);

    @Named("mapEnrollments")
    default List<EnrollmentDTO> mapEnrollments(Set<Enrollment> enrollments) {
        return enrollments == null ? null :
                enrollments.stream()
                        .map(e -> new EnrollmentDTO(
                                e.getId(),
                                null,
                                new ModuleDTO(e.getModule().getId(), null, null, null, null, null),
                                e.getEnrollmentDate(),
                                e.getStatus()
                        ))
                        .collect(Collectors.toList());
    }
}