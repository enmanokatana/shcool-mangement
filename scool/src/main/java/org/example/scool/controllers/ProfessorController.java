package org.example.scool.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.scool.dtos.ProfessorDTO;
import org.example.scool.exceptions.*;
import org.example.scool.services.ProfessorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/professors")
@RequiredArgsConstructor
public class ProfessorController {
    private final ProfessorService professorService;

    @GetMapping
    public String listProfessors(Model model) {
        try {
            List<ProfessorDTO> professors = professorService.getAllProfessors();
            model.addAttribute("professors", professors);
            return "professors/list";
        } catch (DataFetchException ex) {
            model.addAttribute("errorMessage", "Failed to fetch professors. Please try again later.");
            return "professors/list";
        }
    }
    @GetMapping("/profile/{id}")
    public String viewProfessorProfile(@PathVariable Long id, Model model) {
        try {
            ProfessorDTO professorDTO = professorService.getProfessorById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Professor not found with id: " + id));
            model.addAttribute("professor", professorDTO);
            return "professors/view-professor";
        } catch (EntityNotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "redirect:/professors";
        }
    }


    @GetMapping("/add")
    public String showAddProfessorForm(Model model) {
        model.addAttribute("professor", new ProfessorDTO());
        return "professors/add-professor";
    }

    @PostMapping("/add")
    public String addProfessor(@Valid @ModelAttribute("professor") ProfessorDTO professorDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "professors/add-professor";
        }

        try {
            professorService.createProfessor(professorDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Professor added successfully!");
            return "redirect:/professors";
        } catch (DuplicateEntityException ex) {
            result.rejectValue("email", "error.professor", ex.getMessage());
            return "professors/add-professor";
        } catch (ProfessorCreationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create professor. Please try again later.");
            return "redirect:/professors";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditProfessorForm(@PathVariable Long id, Model model) {
        try {
            ProfessorDTO professorDTO = professorService.getProfessorById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Professor not found with id: " + id));
            model.addAttribute("professor", professorDTO);
            return "professors/edit-professor";
        } catch (EntityNotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "redirect:/professors";
        } catch (DataFetchException ex) {
            model.addAttribute("errorMessage", "Failed to fetch professor details. Please try again later.");
            return "redirect:/professors";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateProfessor(@PathVariable Long id,
                                  @Valid @ModelAttribute("professor") ProfessorDTO professorDTO,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "professors/edit-professor";
        }

        try {
            professorService.updateProfessor(id, professorDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Professor updated successfully!");
            return "redirect:/professors";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/professors";
        } catch (DataUpdateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update professor. Please try again later.");
            return "redirect:/professors";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProfessor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            professorService.deleteProfessor(id);
            redirectAttributes.addFlashAttribute("successMessage", "Professor deleted successfully!");
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (IllegalOperationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (DataDeletionException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete professor. Please try again later.");
        }
        return "redirect:/professors";
    }
}