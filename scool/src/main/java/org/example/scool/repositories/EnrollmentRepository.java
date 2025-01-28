package org.example.scool.repositories;

import org.example.scool.models.Enrollment;
import org.example.scool.models.Module;
import org.example.scool.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    @Query("SELECT e FROM Enrollment e WHERE e.student = :student AND e.module = :module")
    Optional<Enrollment> findByStudentAndModule(
            @Param("student") Student student,
            @Param("module") Module module
    );

    List<Enrollment> findByStudent(Student student);
    List<Enrollment> findByModule(Module module);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.status = :status")
    long countByStatus(@Param("status") Enrollment.EnrollmentStatus status);
}