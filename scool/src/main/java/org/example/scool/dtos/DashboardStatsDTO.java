package org.example.scool.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalStudents;
    private long totalProfessors;
    private long totalModules;
    private long totalActiveEnrollments;
}