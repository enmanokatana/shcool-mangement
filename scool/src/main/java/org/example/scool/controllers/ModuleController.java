package org.example.scool.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.scool.dtos.ModuleDTO;
import org.example.scool.dtos.ProfessorDTO;
import org.example.scool.exceptions.*;
import org.example.scool.services.ModuleService;
import org.example.scool.services.ProfessorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/modules")
@RequiredArgsConstructor
public class ModuleController {
    private final ModuleService moduleService;
    private final ProfessorService professorService;

    @GetMapping
    public String listModules(Model model) {
        try {
            List<ModuleDTO> modules = moduleService.getAllModules();
            model.addAttribute("modules", modules);
            return "modules/list";
        } catch (DataFetchException ex) {
            model.addAttribute("errorMessage", "Failed to fetch modules. Please try again later.");
            return "modules/list";
        }
    }

    @GetMapping("/add")
    public String showAddModuleForm(Model model) {
        model.addAttribute("module", new ModuleDTO());
        List<ProfessorDTO> professors = professorService.getAllProfessors();
        model.addAttribute("professors", professors);
        return "modules/add-module";
    }

    @PostMapping("/add")
    public String addModule(@Valid @ModelAttribute("module") ModuleDTO moduleDTO,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<ProfessorDTO> professors = professorService.getAllProfessors();
            model.addAttribute("professors", professors);
            return "modules/add-module";
        }

        try {
            moduleService.createModule(moduleDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Module added successfully!");
            return "redirect:/modules";
        } catch (DuplicateModuleException ex) {
            result.rejectValue("moduleCode", "error.module", ex.getMessage());
            model.addAttribute("professors", professorService.getAllProfessors());
            return "modules/add-module";
        } catch (ModuleCreationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create module. Please try again later.");
            return "redirect:/modules";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditModuleForm(@PathVariable Long id, Model model) {
        try {
            ModuleDTO moduleDTO = moduleService.getModuleById(id);
            List<ProfessorDTO> professors = professorService.getAllProfessors();
            model.addAttribute("module", moduleDTO);
            model.addAttribute("professors", professors);
            return "modules/edit-module";
        } catch (EntityNotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "redirect:/modules";
        } catch (DataFetchException ex) {
            model.addAttribute("errorMessage", "Failed to fetch module details. Please try again later.");
            return "redirect:/modules";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateModule(@PathVariable Long id,
                               @Valid @ModelAttribute("module") ModuleDTO moduleDTO,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<ProfessorDTO> professors = professorService.getAllProfessors();
            model.addAttribute("professors", professors);
            return "modules/edit-module";
        }

        try {
            moduleService.updateModule(id, moduleDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Module updated successfully!");
            return "redirect:/modules";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/modules";
        } catch (DataUpdateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update module. Please try again later.");
            return "redirect:/modules";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteModule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            moduleService.deleteModule(id);
            redirectAttributes.addFlashAttribute("successMessage", "Module deleted successfully!");
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (IllegalOperationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (DataDeletionException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete module. Please try again later.");
        }
        return "redirect:/modules";
    }

}