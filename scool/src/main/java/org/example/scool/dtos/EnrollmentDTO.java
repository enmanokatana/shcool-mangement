package org.example.scool.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.scool.models.Enrollment;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private Long id;
    private StudentDTO student;
    private ModuleDTO module;
    private LocalDate enrollmentDate;
    private Enrollment.EnrollmentStatus status;
}