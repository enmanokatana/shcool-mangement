package org.example.scool.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDTO {
    private Long id;

    @NotBlank(message = "Module code is required")
    @Pattern(regexp = "^MD\\d{4}$", message = "Module code must start with MD followed by 4 digits")
    private String moduleCode;

    @NotBlank(message = "Module name is required")
    @Size(min = 2, max = 100, message = "Module name must be between 2 and 100 characters")
    private String moduleName;

    private String description;
    private ProfessorDTO professor;
    private List<EnrollmentDTO> enrollments;
}
