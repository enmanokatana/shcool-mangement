package org.example.scool.repositories;

import org.example.scool.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
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
}