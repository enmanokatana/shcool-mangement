package org.example.scool.repositories;

import org.example.scool.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentNumber(String studentNumber);
    Optional<Student> findByEmail(String email);

    @Query("SELECT s FROM Student s WHERE s.lastName LIKE %:lastName%")
    List<Student> findByLastNameContaining(String lastName);

    @Query("SELECT COUNT(s) FROM Student s")
    long countTotalStudents();
    List<Student> findByLastNameContainingIgnoreCase(String lastName);

    // New query methods
    List<Student> findByStatus(Student.StudentStatus status);

    Optional<Student> findByEmailAndIdNot(String email, Long id);

    Optional<Student> findByStudentNumberAndIdNot(String studentNumber, Long id);

    @Query("SELECT s FROM Student s WHERE s.enrollmentDate >= :startDate")
    List<Student> findNewStudents(LocalDate startDate);

    @Query("SELECT s FROM Student s WHERE s.status = 'ACTIVE' ORDER BY s.lastName, s.firstName")
    List<Student> findAllActiveStudentsOrdered();
}