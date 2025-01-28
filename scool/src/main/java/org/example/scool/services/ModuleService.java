package org.example.scool.services;

import org.example.scool.dtos.ModuleDTO;
import org.example.scool.dtos.StudentDTO;

import java.util.List;

public interface ModuleService {
    ModuleDTO createModule(ModuleDTO moduleDTO);
    ModuleDTO updateModule(Long id, ModuleDTO moduleDTO);
    void deleteModule(Long id);
    ModuleDTO getModuleById(Long id);
    List<ModuleDTO> getAllModules();
    List<ModuleDTO> searchModulesByName(String name);
    List<StudentDTO> getEnrolledStudents(Long moduleId);
    List<ModuleDTO> getMostPopularModules(int limit);
}
