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
        return student == null ? null :
                new StudentDTO(
                        student.getId(),
                        student.getStudentNumber(),
                        student.getFirstName(),
                        student.getLastName(),
                        student.getEmail(),
                        student.getDateOfBirth(),
                        student.getEnrollmentDate(),
                        null
                );
    }

    @Named("mapModule")
    default ModuleDTO mapModule(Module module) {
        return module == null ? null :
                new ModuleDTO(
                        module.getId(),
                        module.getModuleCode(),
                        module.getModuleName(),
                        module.getDescription(),
                        null,
                        null
                );
    }
}
