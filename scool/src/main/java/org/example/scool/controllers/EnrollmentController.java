package org.example.scool.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.scool.dtos.EnrollmentDTO;
import org.example.scool.dtos.ModuleDTO;
import org.example.scool.dtos.StudentDTO;
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

    /** List Enrollments **/
    @GetMapping
    public String listEnrollments(@RequestParam(value = "studentId", required = false) Long studentId,
                                  @RequestParam(value = "moduleId", required = false) Long moduleId,
                                  Model model) {

        List<EnrollmentDTO> enrollments;

        if (studentId != null) {
            enrollments = enrollmentService.getEnrollmentsByStudent(studentId);
        } else if (moduleId != null) {
            enrollments = enrollmentService.getEnrollmentsByModule(moduleId);
        } else {
            enrollments = enrollmentService.getAllEnrollments(); // Handle general case
        }

        model.addAttribute("enrollments", enrollments);
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("modules", moduleService.getAllModules());
        return "enrollments/list-enrollments";
    }


    /** Show Add Enrollment Form **/
    @GetMapping("/add")
    public String showAddEnrollmentForm(Model model) {
        model.addAttribute("enrollment", new EnrollmentDTO());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("modules", moduleService.getAllModules());
        return "enrollments/add-enrollment";
    }

    /** Add Enrollment **/
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
            redirectAttributes.addFlashAttribute("successMessage", "Enrollment created successfully!");
            return "redirect:/enrollments";
        } catch (Exception ex) {
            result.rejectValue("module", "error.enrollment", ex.getMessage());
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("modules", moduleService.getAllModules());
            return "enrollments/add-enrollment";
        }
    }
    /** Show Edit Enrollment Form **/
    @GetMapping("/edit/{id}")
    public String showEditEnrollmentForm(@PathVariable Long id, Model model) {
        EnrollmentDTO enrollmentDTO = enrollmentService.getEnrollmentById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with id: " + id));

        model.addAttribute("enrollment", enrollmentDTO);
        return "enrollments/edit-enrollment";
    }

    /** Edit Enrollment **/
    @PostMapping("/edit/{id}")
    public String editEnrollment(@PathVariable Long id,
                                 @Valid @ModelAttribute("enrollment") EnrollmentDTO enrollmentDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "enrollments/edit-enrollment";
        }

        try {
            enrollmentService.updateEnrollment(id, enrollmentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Enrollment updated successfully!");
            return "redirect:/enrollments";
        } catch (EntityNotFoundException ex) {
            result.rejectValue("status", "error.enrollment", ex.getMessage());
            return "enrollments/edit-enrollment";
        }
    }

    /** Cancel Enrollment **/
    @GetMapping("/cancel/{id}")
    public String cancelEnrollment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.cancelEnrollment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Enrollment canceled successfully!");
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/enrollments";
    }
}
