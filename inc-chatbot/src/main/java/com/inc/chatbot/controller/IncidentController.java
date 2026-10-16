package com.inc.chatbot.controller;

import com.inc.chatbot.dto.IncidentDto;
import com.inc.chatbot.dto.SolutionDto;
import com.inc.chatbot.entity.Incident;
import com.inc.chatbot.entity.Solution;
import com.inc.chatbot.repository.SolutionRepository;
import com.inc.chatbot.service.AuditService;
import com.inc.chatbot.service.FileStorageService;
import com.inc.chatbot.service.IncidentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/incidents")
public class IncidentController {

    private static final Logger log = LoggerFactory.getLogger(IncidentController.class);

    private final IncidentService incidentService;
    private final SolutionRepository solutionRepository;
    private final FileStorageService fileStorageService;
    private final AuditService auditService;

    public IncidentController(IncidentService incidentService,
                              SolutionRepository solutionRepository,
                              FileStorageService fileStorageService,
                              AuditService auditService) {
        this.incidentService = incidentService;
        this.solutionRepository = solutionRepository;
        this.fileStorageService = fileStorageService;
        this.auditService = auditService;
    }

    @GetMapping
    public String listIncidents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model) {
        Page<Incident> incidents = incidentService.getAllIncidents(page, size);
        model.addAttribute("incidents", incidents);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", incidents.getTotalPages());
        model.addAttribute("totalElements", incidents.getTotalElements());
        model.addAttribute("apps", incidentService.getDistinctApps());
        return "all-incidents";
    }

    @GetMapping("/{id}")
    public String viewIncident(@PathVariable Long id, Model model) {
        Incident incident = incidentService.getByIdWithDetails(id)
                .orElseThrow(() -> new NoSuchElementException("Incident not found: " + id));

        List<Solution> solutions = solutionRepository.findByIncidentIdAndIsActive(id, true);
        List<Solution> allSolutions = solutionRepository.findByIncidentId(id);

        model.addAttribute("incident", incident);
        model.addAttribute("solutions", solutions);
        model.addAttribute("allSolutions", allSolutions);
        model.addAttribute("auditLogs", auditService.getLogsForIncident(id));
        return "incident-detail";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('CONTRIBUTOR','APPROVER','ADMIN')")
    public String newIncidentForm(
            @RequestParam(required = false) String prefill,
            Model model) {
        IncidentDto dto = new IncidentDto();
        if (prefill != null) {
            dto.setIncidentNo(prefill);
        }
        model.addAttribute("incident", dto);
        model.addAttribute("apps", incidentService.getDistinctApps());
        model.addAttribute("statuses", Incident.IncidentStatus.values());
        return "new-incident";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('CONTRIBUTOR','APPROVER','ADMIN')")
    public String saveIncident(
            @Valid @ModelAttribute("incident") IncidentDto dto,
            BindingResult result,
            @RequestParam(required = false) String solutionText,
            @RequestParam(required = false) String solutionType,
            @RequestParam(required = false) MultipartFile[] attachments,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("apps", incidentService.getDistinctApps());
            model.addAttribute("statuses", Incident.IncidentStatus.values());
            return "new-incident";
        }

        try {
            String username = userDetails.getUsername();
            Incident saved = incidentService.createIncident(dto, username);

            if (solutionText != null && !solutionText.isBlank()) {
                SolutionDto sdto = new SolutionDto();
                sdto.setIncidentId(saved.getId());
                sdto.setSolutionText(solutionText);
                sdto.setSolutionType(solutionType != null ? solutionType : "STEPS");
                incidentService.addSolution(saved.getId(), sdto, username);
            }

            if (attachments != null) {
                for (MultipartFile file : attachments) {
                    if (!file.isEmpty()) {
                        try {
                            fileStorageService.storeFile(saved.getId(), file, username);
                        } catch (Exception e) {
                            log.error("Failed to upload file: {}", e.getMessage());
                        }
                    }
                }
            }

            redirectAttributes.addFlashAttribute("successMsg",
                    "Incident " + saved.getIncidentNo() + " created successfully!");
            return "redirect:/incidents/" + saved.getId();

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("apps", incidentService.getDistinctApps());
            model.addAttribute("statuses", Incident.IncidentStatus.values());
            return "new-incident";
        }
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('APPROVER','ADMIN')")
    public String approveIncident(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        incidentService.approveIncident(id, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("successMsg", "Incident approved successfully!");
        return "redirect:/incidents/" + id;
    }

    @PostMapping("/{id}/solution")
    @PreAuthorize("hasAnyRole('CONTRIBUTOR','APPROVER','ADMIN')")
    public String addSolution(
            @PathVariable Long id,
            @ModelAttribute SolutionDto solutionDto,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        solutionDto.setIncidentId(id);
        incidentService.addSolution(id, solutionDto, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("successMsg", "Solution added successfully!");
        return "redirect:/incidents/" + id + "#solution";
    }
}
