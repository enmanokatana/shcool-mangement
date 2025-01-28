package org.example.scool.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.scool.dtos.StudentDTO;
import org.example.scool.exceptions.*;
import org.example.scool.services.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    public String listStudents(Model model) {
        try {
            List<StudentDTO> students = studentService.getAllStudents();
            model.addAttribute("students", students);
            return "students/list";
        } catch (DataFetchException ex) {
            model.addAttribute("errorMessage", "Failed to fetch students. Please try again later.");
            return "students/list";
        }
    }

    @GetMapping("/add")
    public String showAddStudentForm(Model model) {
        model.addAttribute("student", new StudentDTO());
        return "students/add-student";
    }

    @PostMapping("/add")
    public String addStudent(@Valid @ModelAttribute("student") StudentDTO studentDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "students/add-student";
        }

        try {
            studentService.createStudent(studentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Student added successfully!");
            return "redirect:/students";
        } catch (StudentCreationException | DuplicateEntityException ex) {
            result.rejectValue("email", "error.student", ex.getMessage());
            return "students/add-student";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditStudentForm(@PathVariable Long id, Model model) {
        try {
            StudentDTO studentDTO = studentService.getStudentById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
            model.addAttribute("student", studentDTO);
            return "students/edit-student";
        } catch (EntityNotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "redirect:/students";
        } catch (DataFetchException ex) {
            model.addAttribute("errorMessage", "Failed to fetch student details. Please try again later.");
            return "redirect:/students";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateStudent(@PathVariable Long id,
                                @Valid @ModelAttribute("student") StudentDTO studentDTO,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "students/edit-student";
        }

        try {
            studentService.updateStudent(id, studentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
            return "redirect:/students";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/students";
        } catch (DataUpdateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update student. Please try again later.");
            return "redirect:/students";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (IllegalOperationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (DataDeletionException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete student. Please try again later.");
        }
        return "redirect:/students";
    }
}