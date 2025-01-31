package org.example.scool.services;

import java.util.List;
import java.util.Map;

/**
 * Defines the dashboard statistics and data required by the UI.
 */
public interface DashboardService {

    long getTotalStudents();
    long getTotalProfessors();
    long getTotalModules();
    long getTotalEnrollments();

    /**
     * Returns a map of moduleName -> enrollmentCount, sorted by highest enrollment first.
     */
    Map<String, Long> getPopularModules();

    /**
     * Returns a map of statusName -> count. (e.g., ACTIVE=10, COMPLETED=5, DROPPED=3)
     */
    Map<String, Long> getEnrollmentsByStatus();

    /**
     * Returns a list of recent enrollments with all relevant details:
     * studentName, moduleName, enrollmentDate, status
     */
    List<Map<String, Object>> getRecentEnrollments();
}
