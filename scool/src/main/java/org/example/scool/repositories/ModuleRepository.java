package org.example.scool.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.example.scool.models.Module;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByModuleCode(String moduleCode);

    @Query("SELECT m FROM Module m WHERE m.moduleName LIKE %:name%")
    List<Module> findByModuleNameContaining(String name);

    @Query("SELECT m FROM Module m ORDER BY SIZE(m.enrollments) DESC")
    List<Module> findMostPopularModules(Pageable pageable);

    // New method to check if a professor is assigned to any modules
    boolean existsByProfessorId(Long professorId);
}