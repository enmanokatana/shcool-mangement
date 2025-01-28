package org.example.scool.init;


import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.example.scool.models.Student;
import org.example.scool.models.Professor;
import org.example.scool.models.Module;
import org.example.scool.repositories.ModuleRepository;
import org.example.scool.repositories.ProfessorRepository;
import org.example.scool.repositories.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final ModuleRepository moduleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (studentRepository.count() == 0) {
            createInitialStudents();
        }

        if (professorRepository.count() == 0) {
            createInitialProfessors();
        }

        if (moduleRepository.count() == 0) {
            createInitialModules();
        }
    }

    private void createInitialStudents() {
        List<Student> students = Arrays.asList(
                createStudent("ST000001", "John", "Doe", "john.doe@example.com"),
                createStudent("ST000002", "Jane", "Smith", "jane.smith@example.com"),
                createStudent("ST000003", "Mike", "Johnson", "mike.johnson@example.com")
        );
        studentRepository.saveAll(students);
    }

    private Student createStudent(String studentNumber, String firstName, String lastName, String email) {
        Student student = new Student();
        student.setStudentNumber(studentNumber);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setEnrollmentDate(LocalDate.now());
        student.setDateOfBirth(LocalDate.now().minusYears(20));
        return student;
    }

    private void createInitialProfessors() {
        List<Professor> professors = Arrays.asList(
                createProfessor("PR000001", "Alice", "Williams", "alice.williams@example.com", "Computer Science"),
                createProfessor("PR000002", "Bob", "Brown", "bob.brown@example.com", "Mathematics"),
                createProfessor("PR000003", "Charlie", "Davis", "charlie.davis@example.com", "Physics")
        );
        professorRepository.saveAll(professors);
    }

    private Professor createProfessor(String employeeNumber, String firstName, String lastName, String email, String department) {
        Professor professor = new Professor();
        professor.setEmployeeNumber(employeeNumber);
        professor.setFirstName(firstName);
        professor.setLastName(lastName);
        professor.setEmail(email);
        professor.setDepartment(department);
        return professor;
    }

    private void createInitialModules() {
        List<Module> modules = Arrays.asList(
                createModule("MD0001", "Introduction to Programming", "Basic programming concepts"),
                createModule("MD0002", "Advanced Mathematics", "Complex mathematical theories"),
                createModule("MD0003", "Quantum Physics", "Fundamental quantum mechanics")
        );
        moduleRepository.saveAll(modules);
    }

    private Module createModule(String moduleCode, String moduleName, String description) {
        Module module = new Module();
        module.setModuleCode(moduleCode);
        module.setModuleName(moduleName);
        module.setDescription(description);
        return module;
    }
}