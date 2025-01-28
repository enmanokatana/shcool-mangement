package org.example.scool.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.scool.dtos.EnrollmentDTO;
import org.example.scool.exceptions.*;
import org.example.scool.services.EnrollmentService;
import org.example.scool.services.ModuleService;
import org.example.scool.services.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final ModuleService moduleService;

    @GetMapping
    public String listEnrollments(Model model) {
        try {
            long activeEnrollments = enrollmentService.countActiveEnrollments();
            model.addAttribute("activeEnrollments", activeEnrollments);
            return "enrollments/list";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Failed to fetch enrollment data. Please try again later.");
            return "enrollments/list";
        }
    }

    @GetMapping("/add")
    public String showAddEnrollmentForm(Model model) {
        model.addAttribute("enrollment", new EnrollmentDTO());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("modules", moduleService.getAllModules());
        return "enrollments/add-enrollment";
    }

    @PostMapping("/add")
    public String addEnrollment(@Valid @ModelAttribute("enrollment") EnrollmentDTO enrollmentDTO,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("modules", moduleService.getAllModules());
            return "enrollments/add-enrollment";
        }

        try {
            enrollmentService.createEnrollment(enrollmentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Enrollment added successfully!");
            return "redirect:/enrollments";
        } catch (DuplicateEnrollmentException ex) {
            result.rejectValue("module", "error.enrollment", ex.getMessage());
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("modules", moduleService.getAllModules());
            return "enrollments/add-enrollment";
        } catch (EnrollmentCreationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create enrollment. Please try again later.");
            return "redirect:/enrollments";
        }
    }

    @GetMapping("/by-student/{studentId}")
    public String getEnrollmentsByStudent(@PathVariable Long studentId, Model model) {
        try {
            List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);
            model.addAttribute("enrollments", enrollments);
            return "enrollments/enrollments-by-student";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Failed to fetch enrollments for the student. Please try again later.");
            return "enrollments/enrollments-by-student";
        }
    }

    @GetMapping("/by-module/{moduleId}")
    public String getEnrollmentsByModule(@PathVariable Long moduleId, Model model) {
        try {
            List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByModule(moduleId);
            model.addAttribute("enrollments", enrollments);
            return "enrollments/enrollments-by-module";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Failed to fetch enrollments for the module. Please try again later.");
            return "enrollments/enrollments-by-module";
        }
    }

    @GetMapping("/cancel/{enrollmentId}")
    public String cancelEnrollment(@PathVariable Long enrollmentId, RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.cancelEnrollment(enrollmentId);
            redirectAttributes.addFlashAttribute("successMessage", "Enrollment canceled successfully!");
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel enrollment. Please try again later.");
        }
        return "redirect:/enrollments";
    }
}