package org.example.scool.mappers;


import org.example.scool.dtos.ModuleDTO;
import org.example.scool.dtos.ProfessorDTO;
import org.example.scool.models.Professor;
import org.example.scool.models.Module;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfessorMapper {
    @Mapping(target = "modules", qualifiedByName = "mapModules")
    ProfessorDTO toDTO(Professor professor);
    Professor toEntity(ProfessorDTO professorDTO);

    void updateEntityFromDTO(ProfessorDTO dto, @MappingTarget Professor entity);

    @Named("mapModules")
    default List<ModuleDTO> mapModules(Set<Module> modules) {
        return modules == null ? null :
                modules.stream()
                        .map(m -> new ModuleDTO(
                                m.getId(),
                                m.getModuleCode(),
                                m.getModuleName(),
                                m.getDescription(),
                                null,
                                null
                        ))
                        .collect(Collectors.toList());
    }
}