package org.example.scool.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scool.models.Enrollment;
import org.example.scool.repositories.EnrollmentRepository;
import org.example.scool.repositories.ModuleRepository;
import org.example.scool.repositories.ProfessorRepository;
import org.example.scool.repositories.StudentRepository;
import org.example.scool.services.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Concrete implementation of DashboardService.
 * Fetches and aggregates data for the dashboard.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final ModuleRepository moduleRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public long getTotalStudents() {
        long count = studentRepository.count();
        log.info("Total students: {}", count);
        return count;
    }

    @Override
    public long getTotalProfessors() {
        long count = professorRepository.count();
        log.info("Total professors: {}", count);
        return count;
    }

    @Override
    public long getTotalModules() {
        long count = moduleRepository.count();
        log.info("Total modules: {}", count);
        return count;
    }

    @Override
    public long getTotalEnrollments() {
        long count = enrollmentRepository.count();
        log.info("Total enrollments: {}", count);
        return count;
    }

    /**
     * Returns a map of (moduleName -> enrollmentCount).
     * Example query in EnrollmentRepository:
     * {@code SELECT e.module.moduleName AS moduleName, COUNT(e.id) AS enrollmentCount ...}
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getPopularModules() {
        log.info("Fetching popular modules...");
        List<Map<String, Object>> rawData = enrollmentRepository.findPopularModules();
        // -> Each map has keys: "moduleName" and "enrollmentCount"

        // Convert raw data into a sorted map by enrollment count desc
        // (Map<String, Long>) moduleName -> enrollmentCount
        return rawData.stream()
                .sorted((a, b) -> Long.compare(
                        ((Number)b.get("enrollmentCount")).longValue(),
                        ((Number)a.get("enrollmentCount")).longValue()
                ))
                .collect(Collectors.toMap(
                        e -> (String) e.get("moduleName"),
                        e -> ((Number) e.get("enrollmentCount")).longValue(),
                        (oldVal, newVal) -> oldVal, // merge function
                        LinkedHashMap::new // maintain insertion order
                ));
    }

    /**
     * Returns a map of (statusName -> count).
     * If your repository query returns the status as an enum, we must convert with .name().
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getEnrollmentsByStatus() {
        log.info("Fetching enrollments by status...");
        List<Map<String, Object>> rawData = enrollmentRepository.countEnrollmentsByStatus();
        // -> Each map has keys: "status" (enum) and "count"

        return rawData.stream()
                .collect(Collectors.toMap(
                        e -> ((Enrollment.EnrollmentStatus) e.get("status")).name(),
                        e -> ((Number) e.get("count")).longValue()
                ));
    }

    /**
     * Returns a list of recent enrollments with:
     *  studentName, moduleName, enrollmentDate, status
     */
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRecentEnrollments() {
        log.info("Fetching recent enrollments...");
        List<Map<String, Object>> list = enrollmentRepository.findRecentEnrollments();
        log.info("Recent enrollments found: {}", list.size());
        return list;
    }
}
