package org.example.scool.repositories;

import org.example.scool.models.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByEmployeeNumber(String employeeNumber);
    Optional<Professor> findByEmail(String email);

    @Query("SELECT p FROM Professor p WHERE p.department = :department")
    List<Professor> findByDepartment(String department);
}
