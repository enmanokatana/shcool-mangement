package org.example.scool.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scool.dtos.ProfessorDTO;
import org.example.scool.exceptions.*;
import org.example.scool.mappers.ProfessorMapper;
import org.example.scool.models.Professor;
import org.example.scool.models.Module;
import org.example.scool.repositories.ModuleRepository;
import org.example.scool.repositories.ProfessorRepository;
import org.example.scool.services.ProfessorService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ProfessorServiceImpl implements ProfessorService {
    private final ProfessorRepository professorRepository;
    private final ModuleRepository moduleRepository;
    private final ProfessorMapper professorMapper;

    @Override
    public ProfessorDTO createProfessor(ProfessorDTO professorDTO) throws ProfessorCreationException {
        try {
            validateUniqueProfessorConstraints(professorDTO); // Validate unique constraints
            Professor professor = professorMapper.toEntity(professorDTO);
            Professor savedProfessor = professorRepository.save(professor);
            return professorMapper.toDTO(savedProfessor);
        } catch (DuplicateEntityException ex) {
            log.error("Duplicate entity error: {}", ex.getMessage());
            throw ex; // Re-throw the exception
        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation: {}", ex.getMessage());
            throw new ProfessorCreationException("Unable to create professor due to data integrity violation", ex);
        } catch (Exception ex) {
            log.error("Error creating professor: {}", ex.getMessage());
            throw new ProfessorCreationException("Failed to create professor", (DataIntegrityViolationException) ex);
        }
    }

    @Override
    public ProfessorDTO updateProfessor(Long id, ProfessorDTO professorDTO) {
        try {
            Professor existingProfessor = professorRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Professor not found with id: " + id));

            // Update fields
            existingProfessor.setEmployeeNumber(professorDTO.getEmployeeNumber());
            existingProfessor.setFirstName(professorDTO.getFirstName());
            existingProfessor.setLastName(professorDTO.getLastName());
            existingProfessor.setEmail(professorDTO.getEmail());
            existingProfessor.setDepartment(professorDTO.getDepartment());

            Professor updatedProfessor = professorRepository.save(existingProfessor);
            return professorMapper.toDTO(updatedProfessor);
        } catch (EntityNotFoundException ex) {
            log.error("Professor not found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating professor: {}", ex.getMessage());
            throw new DataUpdateException("Failed to update professor", ex);
        }
    }

    @Override
    public void deleteProfessor(Long id) {
        try {
            Professor professor = professorRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Professor not found with id: " + id));

            // Check if the professor is assigned to any modules
            boolean hasAssignedModules = moduleRepository.existsByProfessorId(id);

            if (hasAssignedModules) {
                throw new IllegalOperationException("Cannot delete professor assigned to modules");
            }

            professorRepository.delete(professor);
        } catch (EntityNotFoundException ex) {
            log.error("Professor not found: {}", ex.getMessage());
            throw ex;
        } catch (IllegalOperationException ex) {
            log.error("Illegal operation: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error deleting professor: {}", ex.getMessage());
            throw new DataDeletionException("Failed to delete professor", ex);
        }
    }

    @Override
    public Optional<ProfessorDTO> getProfessorById(Long id) {
        try {
            Optional<Professor> professor = professorRepository.findById(id);
            return professor.map(professorMapper::toDTO);
        } catch (Exception ex) {
            log.error("Error fetching professor by ID: {}", ex.getMessage());
            throw new DataFetchException("Failed to fetch professor by ID", ex);
        }
    }

    @Override
    public List<ProfessorDTO> getAllProfessors() {
        try {
            List<Professor> professors = professorRepository.findAll();
            return professors.stream()
                    .map(professorMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error fetching all professors: {}", ex.getMessage());
            throw new DataFetchException("Failed to fetch professors", ex);
        }
    }

    @Override
    public List<ProfessorDTO> getProfessorsByDepartment(String department) {
        try {
            List<Professor> professors = professorRepository.findByDepartment(department);
            return professors.stream()
                    .map(professorMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error fetching professors by department: {}", ex.getMessage());
            throw new DataFetchException("Failed to fetch professors by department", ex);
        }
    }

    @Override
    public void assignModuleToProfessor(Long professorId, Long moduleId) throws ModuleAssignmentException {
        try {
            Professor professor = professorRepository.findById(professorId)
                    .orElseThrow(() -> new EntityNotFoundException("Professor not found with id: " + professorId));
            Module module = moduleRepository.findById(moduleId)
                    .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + moduleId));

            module.setProfessor(professor);
            moduleRepository.save(module);
        } catch (EntityNotFoundException ex) {
            log.error("Entity not found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error assigning module to professor: {}", ex.getMessage());
            throw new ModuleAssignmentException("Failed to assign module to professor", ex);
        }
    }

    private void validateUniqueProfessorConstraints(ProfessorDTO professorDTO) {
        professorRepository.findByEmail(professorDTO.getEmail())
                .ifPresent(p -> {
                    throw new DuplicateEntityException("Email already exists: " + professorDTO.getEmail());
                });

        professorRepository.findByEmployeeNumber(professorDTO.getEmployeeNumber())
                .ifPresent(p -> {
                    throw new DuplicateEntityException("Employee number already exists: " + professorDTO.getEmployeeNumber());
                });
    }
}