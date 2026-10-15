package com.inc.chatbot.controller;

import com.inc.chatbot.dto.IncidentDto;
import com.inc.chatbot.dto.SearchRequest;
import com.inc.chatbot.dto.SearchResponse;
import com.inc.chatbot.dto.SolutionDto;
import com.inc.chatbot.entity.Incident;
import com.inc.chatbot.service.FileStorageService;
import com.inc.chatbot.service.IncidentService;
import com.inc.chatbot.service.SearchService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);

    private final SearchService searchService;
    private final IncidentService incidentService;
    private final FileStorageService fileStorageService;

    public ApiController(SearchService searchService,
                         IncidentService incidentService,
                         FileStorageService fileStorageService) {
        this.searchService = searchService;
        this.incidentService = incidentService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/search")
    public ResponseEntity<SearchResponse> search(@RequestBody SearchRequest request) {
        try {
            SearchResponse response = searchService.search(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Search error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/incidents")
    public ResponseEntity<?> listIncidents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Incident> incidents = incidentService.getAllIncidents(page, size);
            Map<String, Object> response = new HashMap<>();
            response.put("content", incidents.getContent());
            response.put("totalElements", incidents.getTotalElements());
            response.put("totalPages", incidents.getTotalPages());
            response.put("currentPage", page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/incidents/{id}")
    public ResponseEntity<?> getIncident(@PathVariable Long id) {
        return incidentService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/incidents")
    @PreAuthorize("hasAnyRole('CONTRIBUTOR','APPROVER','ADMIN')")
    public ResponseEntity<?> createIncident(
            @Valid @RequestBody IncidentDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Incident created = incidentService.createIncident(dto, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Create incident error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/incidents/{id}/attachments")
    @PreAuthorize("hasAnyRole('CONTRIBUTOR','APPROVER','ADMIN')")
    public ResponseEntity<?> uploadAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            var attachment = fileStorageService.storeFile(id, file, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(attachment);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Upload error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/incidents/{id}/approve")
    @PreAuthorize("hasAnyRole('APPROVER','ADMIN')")
    public ResponseEntity<?> approveIncident(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Incident approved = incidentService.approveIncident(id, userDetails.getUsername());
            return ResponseEntity.ok(Map.of("message", "Incident approved", "id", approved.getId()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/incidents/{id}/solution")
    @PreAuthorize("hasAnyRole('CONTRIBUTOR','APPROVER','ADMIN')")
    public ResponseEntity<?> addSolution(
            @PathVariable Long id,
            @RequestBody SolutionDto solutionDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            solutionDto.setIncidentId(id);
            var solution = incidentService.addSolution(id, solutionDto, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(solution);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/apps")
    public ResponseEntity<?> getApps() {
        return ResponseEntity.ok(incidentService.getDistinctApps());
    }
}
