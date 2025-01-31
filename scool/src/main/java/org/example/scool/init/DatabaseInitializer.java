package org.example.scool.init;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.example.scool.models.*;
import org.example.scool.models.Enrollment.EnrollmentStatus;
import org.example.scool.models.Module;
import org.example.scool.models.Student.StudentStatus;
import org.example.scool.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final ModuleRepository moduleRepository;
    private final EnrollmentRepository enrollmentRepository;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(adminRole));

            userRepository.save(admin);
        }
    }
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

        if (enrollmentRepository.count() == 0) {
            createInitialEnrollments();
        }
    }

    private void createInitialStudents() {
        List<Student> students = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            String studentNumber = String.format("ST%06d", i);
            students.add(createStudent(
                    studentNumber,
                    "Student" + i,
                    "Last" + i,
                    "student" + i + "@example.com",
                    i % 2 == 0 ? "Male" : "Female"
            ));
        }
        studentRepository.saveAll(students);
    }

    private Student createStudent(String studentNumber, String firstName, String lastName, String email, String gender) {
        Student student = new Student();
        student.setStudentNumber(studentNumber);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setGender(gender);

        // Defaults
        student.setEnrollmentDate(LocalDate.now());
        student.setDateOfBirth(LocalDate.now().minusYears(18 + (int) (Math.random() * 5))); // Random age 18-23
        student.setProfilePicture("default-profile.png");
        student.setPhoneNumber("123-456-" + String.format("%04d", (int) (Math.random() * 10000)));
        student.setAddress("123 Default St, Example City");
        student.setEmergencyContact("Contact for " + firstName);
        student.setEmergencyPhone("987-654-" + String.format("%04d", (int) (Math.random() * 10000)));
        student.setStatus(StudentStatus.ACTIVE); // Default status

        return student;
    }

    private void createInitialProfessors() {
        List<Professor> professors = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            String employeeNumber = String.format("PR%06d", i);
            professors.add(createProfessor(
                    employeeNumber,
                    "Professor" + i,
                    "Last" + i,
                    "professor" + i + "@example.com",
                    "Department" + ((i % 5) + 1) // Rotate departments
            ));
        }
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
        List<Module> modules = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            String moduleCode = String.format("MD%04d", i);
            modules.add(createModule(
                    moduleCode,
                    "Module " + i,
                    "Description for Module " + i
            ));
        }
        moduleRepository.saveAll(modules);
    }

    private Module createModule(String moduleCode, String moduleName, String description) {
        Module module = new Module();
        module.setModuleCode(moduleCode);
        module.setModuleName(moduleName);
        module.setDescription(description);
        return module;
    }

    private void createInitialEnrollments() {
        List<Student> students = studentRepository.findAll();
        List<Module> modules = moduleRepository.findAll();
        List<Enrollment> enrollments = new ArrayList<>();

        Random random = new Random();

        // Randomly assign students to modules
        for (Student student : students) {
            int moduleCount = 2 + random.nextInt(4); // Each student takes 2-5 modules
            Set<Module> assignedModules = new HashSet<>();
            while (assignedModules.size() < moduleCount) {
                Module module = modules.get(random.nextInt(modules.size()));
                assignedModules.add(module);
            }

            for (Module module : assignedModules) {
                enrollments.add(createEnrollment(student, module));
            }
        }

        enrollmentRepository.saveAll(enrollments);
    }

    private Enrollment createEnrollment(Student student, Module module) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setModule(module);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        return enrollment;
    }
}
