package org.example.scool.repositories;

import org.example.scool.models.Enrollment;
import org.example.scool.models.Module;
import org.example.scool.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
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
    Boolean existsByStudentAndModule(Student s,Module m );
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByModuleId(Long moduleId);
    List<Enrollment> findByStudentIdAndModuleId(Long studentId, Long moduleId);  // New method
    // Find the most popular modules by counting enrollments per module




    /**
     * Most popular modules: returns moduleName + enrollmentCount
     */
    @Query("""
        SELECT m.moduleName AS moduleName, COUNT(e.id) AS enrollmentCount
        FROM Enrollment e JOIN e.module m
        GROUP BY m.moduleName
        """)
    List<Map<String, Object>> findPopularModules();

    /**
     * Returns status + count for each.
     * Notice 'status' is an enum field on Enrollment.
     */
    @Query("""
        SELECT e.status AS status, COUNT(e.id) AS count
        FROM Enrollment e
        GROUP BY e.status
        """)
    List<Map<String, Object>> countEnrollmentsByStatus();

    /**
     * Return the most recent enrollments (with student & module info).
     * The keys must match your service usage: "studentName", "moduleName", "enrollmentDate", "status".
     */
    @Query("""
        SELECT CONCAT(s.firstName, ' ', s.lastName) AS studentName,
               m.moduleName AS moduleName,
               e.enrollmentDate AS enrollmentDate,
               e.status AS status
        FROM Enrollment e 
        JOIN e.student s
        JOIN e.module m
        ORDER BY e.enrollmentDate DESC
        """)
    List<Map<String, Object>> findRecentEnrollments();

}