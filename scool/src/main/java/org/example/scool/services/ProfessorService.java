package org.example.scool.services;

import org.example.scool.dtos.ProfessorDTO;
import org.example.scool.exceptions.ModuleAssignmentException;
import org.example.scool.exceptions.ProfessorCreationException;

import java.util.List;
import java.util.Optional;

public interface ProfessorService {
    ProfessorDTO createProfessor(ProfessorDTO professorDTO) throws ProfessorCreationException;
    ProfessorDTO updateProfessor(Long id, ProfessorDTO professorDTO);
    void deleteProfessor(Long id);
    Optional<ProfessorDTO> getProfessorById(Long id);
    List<ProfessorDTO> getAllProfessors();
    List<ProfessorDTO> getProfessorsByDepartment(String department);
    void assignModuleToProfessor(Long professorId, Long moduleId) throws ModuleAssignmentException;
}
