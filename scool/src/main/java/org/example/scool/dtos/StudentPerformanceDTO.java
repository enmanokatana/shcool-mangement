package org.example.scool.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentPerformanceDTO {
    private String studentNumber;
    private String studentName;
    private int totalModulesEnrolled;
    private double averagePerformance;
}