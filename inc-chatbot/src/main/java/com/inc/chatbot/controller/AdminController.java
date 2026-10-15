package com.inc.chatbot.controller;

import com.inc.chatbot.entity.User;
import com.inc.chatbot.service.AuditService;
import com.inc.chatbot.service.IncidentService;
import com.inc.chatbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;
    private final IncidentService incidentService;
    private final AuditService auditService;

    public AdminController(UserService userService,
                           IncidentService incidentService,
                           AuditService auditService) {
        this.userService = userService;
        this.incidentService = incidentService;
        this.auditService = auditService;
    }

    @GetMapping
    public String adminDashboard(
            @RequestParam(defaultValue = "0") int auditPage,
            Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("auditLogs", auditService.getAuditLogs(auditPage, 20));
        model.addAttribute("totalIncidents", incidentService.countAll());
        model.addAttribute("approvedIncidents", incidentService.countApproved());
        model.addAttribute("roles", User.UserRole.values());
        model.addAttribute("auditPage", auditPage);
        return "admin";
    }

    @PostMapping("/users/create")
    public String createUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String role,
            RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(username, password, email, User.UserRole.valueOf(role));
            redirectAttributes.addFlashAttribute("successMsg", "User '" + username + "' created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error creating user: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUserActive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.toggleUserActive(id);
            redirectAttributes.addFlashAttribute("successMsg",
                    "User '" + user.getUsername() + "' " + (user.getIsActive() ? "activated" : "deactivated") + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(
            @PathVariable Long id,
            @RequestParam String role,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRole(id, User.UserRole.valueOf(role));
            redirectAttributes.addFlashAttribute("successMsg", "User role updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/incidents/{id}/delete")
    public String deleteIncident(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            incidentService.deleteIncident(id);
            redirectAttributes.addFlashAttribute("successMsg", "Incident deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error: " + e.getMessage());
        }
        return "redirect:/admin";
    }
}
