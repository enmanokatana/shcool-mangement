package org.example.scool.controllers;

import lombok.RequiredArgsConstructor;
import org.example.scool.dtos.DashboardStatsDTO;
import org.example.scool.dtos.ModulePopularityDTO;
import org.example.scool.dtos.StudentPerformanceDTO;
import org.example.scool.services.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public String dashboard(Model model) {
        // Fetch dashboard statistics
        DashboardStatsDTO stats = dashboardService.getDashboardStatistics();
        model.addAttribute("stats", stats);

        // Fetch module popularity report
        List<ModulePopularityDTO> modulePopularity = dashboardService.getModulePopularityReport();
        model.addAttribute("modulePopularity", modulePopularity);

        // Fetch student performance report
        List<StudentPerformanceDTO> studentPerformance = dashboardService.getStudentPerformanceReport();
        model.addAttribute("studentPerformance", studentPerformance);

        return "dashboard/index";
    }
}