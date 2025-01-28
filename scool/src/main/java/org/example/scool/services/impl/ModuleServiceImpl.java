package org.example.scool.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scool.dtos.ModuleDTO;
import org.example.scool.dtos.StudentDTO;
import org.example.scool.exceptions.*;
import org.example.scool.mappers.ModuleMapper;
import org.example.scool.models.Module;
import org.example.scool.models.Professor;
import org.example.scool.repositories.ModuleRepository;
import org.example.scool.repositories.ProfessorRepository;
import org.example.scool.services.ModuleService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {
    private final ModuleRepository moduleRepository;
    private final ProfessorRepository professorRepository;
    private final ModuleMapper moduleMapper;

    @Override
    public ModuleDTO createModule(ModuleDTO moduleDTO) {
        try {
            // Validate unique module code
            if (moduleRepository.findByModuleCode(moduleDTO.getModuleCode()).isPresent()) {
                throw new DuplicateModuleException("Module code already exists: " + moduleDTO.getModuleCode());
            }

            Module module = moduleMapper.toEntity(moduleDTO);

            // If professor is specified, validate and attach
            if (moduleDTO.getProfessor() != null && moduleDTO.getProfessor().getId() != null) {
                Professor professor = professorRepository.findById(moduleDTO.getProfessor().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Professor not found"));
                module.setProfessor(professor);
            }

            Module savedModule = moduleRepository.save(module);
            return moduleMapper.toDTO(savedModule);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error creating module: {}", ex.getMessage());
            throw new ModuleCreationException("Unable to create module", ex);
        }
    }

    @Override
    public ModuleDTO updateModule(Long id, ModuleDTO moduleDTO) {
        try {
            Module existingModule = moduleRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + id));

            // Update fields
            existingModule.setModuleCode(moduleDTO.getModuleCode());
            existingModule.setModuleName(moduleDTO.getModuleName());
            existingModule.setDescription(moduleDTO.getDescription());

            // If professor is specified, validate and attach
            if (moduleDTO.getProfessor() != null && moduleDTO.getProfessor().getId() != null) {
                Professor professor = professorRepository.findById(moduleDTO.getProfessor().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Professor not found"));
                existingModule.setProfessor(professor);
            } else {
                existingModule.setProfessor(null);
            }

            Module updatedModule = moduleRepository.save(existingModule);
            return moduleMapper.toDTO(updatedModule);
        } catch (EntityNotFoundException ex) {
            log.error("Module not found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating module: {}", ex.getMessage());
            throw new DataUpdateException("Failed to update module", ex);
        }
    }

    @Override
    public void deleteModule(Long id) {
        try {
            Module module = moduleRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + id));

            // Check if the module has any enrollments
            boolean hasEnrollments = !module.getEnrollments().isEmpty();

            if (hasEnrollments) {
                throw new IllegalOperationException("Cannot delete module with active enrollments");
            }

            moduleRepository.delete(module);
        } catch (EntityNotFoundException ex) {
            log.error("Module not found: {}", ex.getMessage());
            throw ex;
        } catch (IllegalOperationException ex) {
            log.error("Illegal operation: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error deleting module: {}", ex.getMessage());
            throw new DataDeletionException("Failed to delete module", ex);
        }
    }

    @Override
    public ModuleDTO getModuleById(Long id) {
        try {
            Module module = moduleRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + id));
            return moduleMapper.toDTO(module);
        } catch (EntityNotFoundException ex) {
            log.error("Module not found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error fetching module by ID: {}", ex.getMessage());
            throw new DataFetchException("Failed to fetch module by ID", ex);
        }
    }

    @Override
    public List<ModuleDTO> getAllModules() {
        try {
            List<Module> modules = moduleRepository.findAll();
            return modules.stream()
                    .map(moduleMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error fetching all modules: {}", ex.getMessage());
            throw new DataFetchException("Failed to fetch modules", ex);
        }
    }

    @Override
    public List<ModuleDTO> searchModulesByName(String name) {
        try {
            List<Module> modules = moduleRepository.findByModuleNameContaining(name);
            return modules.stream()
                    .map(moduleMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error searching modules by name: {}", ex.getMessage());
            throw new DataFetchException("Failed to search modules by name", ex);
        }
    }

    @Override
    public List<StudentDTO> getEnrolledStudents(Long moduleId) {
        try {
            Module module = moduleRepository.findById(moduleId)
                    .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + moduleId));

            return module.getEnrollments().stream()
                    .map(enrollment -> new StudentDTO(
                            enrollment.getStudent().getId(),
                            enrollment.getStudent().getStudentNumber(),
                            enrollment.getStudent().getFirstName(),
                            enrollment.getStudent().getLastName(),
                            enrollment.getStudent().getEmail(),
                            null, null, null
                    ))
                    .collect(Collectors.toList());
        } catch (EntityNotFoundException ex) {
            log.error("Module not found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error fetching enrolled students: {}", ex.getMessage());
            throw new DataFetchException("Failed to fetch enrolled students", ex);
        }
    }

    @Override
    public List<ModuleDTO> getMostPopularModules(int limit) {
        try {
            List<Module> modules = moduleRepository.findMostPopularModules(PageRequest.of(0, limit));
            return modules.stream()
                    .map(moduleMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error fetching most popular modules: {}", ex.getMessage());
            throw new DataFetchException("Failed to fetch most popular modules", ex);
        }
    }
}