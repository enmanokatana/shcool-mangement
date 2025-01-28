package org.example.scool.services;

import org.example.scool.dtos.DashboardStatsDTO;
import org.example.scool.dtos.ModulePopularityDTO;
import org.example.scool.dtos.StudentPerformanceDTO;

import java.util.List;

public interface DashboardService {
    DashboardStatsDTO getDashboardStatistics();
    List<ModulePopularityDTO> getModulePopularityReport();
    List<StudentPerformanceDTO> getStudentPerformanceReport();
}
