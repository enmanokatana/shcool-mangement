package org.example.scool.controllers;

import lombok.RequiredArgsConstructor;
import org.example.scool.services.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the system's main Dashboard.
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Basic counters
        model.addAttribute("totalStudents", dashboardService.getTotalStudents());
        model.addAttribute("totalProfessors", dashboardService.getTotalProfessors());
        model.addAttribute("totalModules", dashboardService.getTotalModules());
        model.addAttribute("totalEnrollments", dashboardService.getTotalEnrollments());

        // Chart data
        model.addAttribute("popularModules", dashboardService.getPopularModules());
        model.addAttribute("enrollmentsByStatus", dashboardService.getEnrollmentsByStatus());
        model.addAttribute("recentEnrollments", dashboardService.getRecentEnrollments());

        // Optional: Additional data if you want a daily chart, etc.

        return "dashboard/dashboard";
    }
    @GetMapping("/")
    public String showDashboardAsHome(Model model) {
        // Basic counters
        model.addAttribute("totalStudents", dashboardService.getTotalStudents());
        model.addAttribute("totalProfessors", dashboardService.getTotalProfessors());
        model.addAttribute("totalModules", dashboardService.getTotalModules());
        model.addAttribute("totalEnrollments", dashboardService.getTotalEnrollments());

        // Chart data
        model.addAttribute("popularModules", dashboardService.getPopularModules());
        model.addAttribute("enrollmentsByStatus", dashboardService.getEnrollmentsByStatus());
        model.addAttribute("recentEnrollments", dashboardService.getRecentEnrollments());

        // Optional: Additional data if you want a daily chart, etc.

        return "dashboard/dashboard";
    }
}
