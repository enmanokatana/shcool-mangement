package org.example.scool.dtos;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDTO {
    private Long id;

    @NotBlank(message = "Student number is required")
    @Pattern(regexp = "^ST\\d{6}$", message = "Student number must start with ST followed by 6 digits")
    private String studentNumber;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private LocalDate enrollmentDate;

    @Valid
    private List<EnrollmentDTO> enrollments;
    private String phoneNumber;
    private String address;
    private String emergencyContact;

    private String emergencyPhone;
    private String gender;
    private String status;
    // In StudentDTO.java
    private String profilePicture;


}