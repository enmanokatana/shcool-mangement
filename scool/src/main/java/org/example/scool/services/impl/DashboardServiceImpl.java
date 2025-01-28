package org.example.scool.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scool.dtos.DashboardStatsDTO;
import org.example.scool.dtos.ModulePopularityDTO;
import org.example.scool.dtos.StudentPerformanceDTO;
import org.example.scool.models.Enrollment;
import org.example.scool.models.Module;
import org.example.scool.models.Student;
import org.example.scool.repositories.EnrollmentRepository;
import org.example.scool.repositories.ModuleRepository;
import org.example.scool.repositories.ProfessorRepository;
import org.example.scool.repositories.StudentRepository;
import org.example.scool.services.DashboardService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final ModuleRepository moduleRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public DashboardStatsDTO getDashboardStatistics() {
        long totalStudents = studentRepository.count();
        long totalProfessors = professorRepository.count();
        long totalModules = moduleRepository.count();
        long totalActiveEnrollments = enrollmentRepository.countByStatus(Enrollment.EnrollmentStatus.ACTIVE);

        return new DashboardStatsDTO(totalStudents, totalProfessors, totalModules, totalActiveEnrollments);
    }

    @Override
    public List<ModulePopularityDTO> getModulePopularityReport() {
        List<Module> modules = moduleRepository.findAll();
        return modules.stream()
                .map(module -> new ModulePopularityDTO(
                        module.getModuleCode(),
                        module.getModuleName(),
                        module.getEnrollments().size()
                ))
                .sorted(Comparator.comparingLong(ModulePopularityDTO::getEnrollmentCount).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentPerformanceDTO> getStudentPerformanceReport() {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .map(student -> {
                    int totalModulesEnrolled = student.getEnrollments().size();
                    double averagePerformance = 0.0; // Placeholder for now

                    return new StudentPerformanceDTO(
                            student.getStudentNumber(),
                            student.getFirstName() + " " + student.getLastName(),
                            totalModulesEnrolled,
                            averagePerformance
                    );
                })
                .sorted(Comparator.comparingDouble(StudentPerformanceDTO::getAveragePerformance).reversed())
                .collect(Collectors.toList());
    }}