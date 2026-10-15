package com.inc.chatbot.service;

import com.inc.chatbot.dto.IncidentDto;
import com.inc.chatbot.dto.SolutionDto;
import com.inc.chatbot.entity.Incident;
import com.inc.chatbot.entity.Solution;
import com.inc.chatbot.repository.IncidentRepository;
import com.inc.chatbot.repository.SolutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class IncidentService {

    private static final Logger log = LoggerFactory.getLogger(IncidentService.class);

    private final IncidentRepository incidentRepository;
    private final SolutionRepository solutionRepository;
    private final AuditService auditService;

    public IncidentService(IncidentRepository incidentRepository,
                           SolutionRepository solutionRepository,
                           AuditService auditService) {
        this.incidentRepository = incidentRepository;
        this.solutionRepository = solutionRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<Incident> getAllIncidents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return incidentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Incident> getById(Long id) {
        return incidentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Incident> getByIncidentNo(String incidentNo) {
        return incidentRepository.findByIncidentNo(incidentNo);
    }

    @Transactional
    public Incident createIncident(IncidentDto dto, String createdBy) {
        if (incidentRepository.findByIncidentNo(dto.getIncidentNo()).isPresent()) {
            throw new IllegalArgumentException("Incident number already exists: " + dto.getIncidentNo());
        }

        Incident incident = Incident.builder()
                .incidentNo(dto.getIncidentNo())
                .taskNo(dto.getTaskNo())
                .app(dto.getApp())
                .description(dto.getDescription())
                .status(dto.getStatus() != null
                        ? Incident.IncidentStatus.valueOf(dto.getStatus())
                        : Incident.IncidentStatus.DRAFT)
                .lastComment(dto.getLastComment())
                .comments(dto.getComments())
                .rca(dto.getRca())
                .planSolution(dto.getPlanSolution())
                .createdBy(createdBy)
                .isApproved(false)
                .build();

        Incident saved = incidentRepository.save(incident);
        log.info("Created incident: {} by {}", saved.getIncidentNo(), createdBy);
        auditService.log(null, "CREATE_INCIDENT", "INCIDENT", saved.getId(),
                "Created incident " + saved.getIncidentNo(), null);
        return saved;
    }

    @Transactional
    public Incident updateIncident(Long id, IncidentDto dto, String updatedBy) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Incident not found: " + id));

        incident.setApp(dto.getApp());
        incident.setDescription(dto.getDescription());
        incident.setLastComment(dto.getLastComment());
        incident.setComments(dto.getComments());
        incident.setRca(dto.getRca());
        incident.setPlanSolution(dto.getPlanSolution());

        if (dto.getStatus() != null) {
            incident.setStatus(Incident.IncidentStatus.valueOf(dto.getStatus()));
        }
        if (dto.getTaskNo() != null) {
            incident.setTaskNo(dto.getTaskNo());
        }

        Incident saved = incidentRepository.save(incident);
        auditService.log(null, "UPDATE_INCIDENT", "INCIDENT", saved.getId(),
                "Updated incident " + saved.getIncidentNo(), null);
        return saved;
    }

    @Transactional
    public Incident approveIncident(Long id, String approvedBy) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Incident not found: " + id));

        incident.setIsApproved(true);
        if (incident.getStatus() == Incident.IncidentStatus.DRAFT) {
            incident.setStatus(Incident.IncidentStatus.OPEN);
        }

        Incident saved = incidentRepository.save(incident);
        auditService.log(null, "APPROVE_INCIDENT", "INCIDENT", saved.getId(),
                "Approved incident " + saved.getIncidentNo() + " by " + approvedBy, null);
        return saved;
    }

    @Transactional
    public Solution addSolution(Long incidentId, SolutionDto dto, String createdBy) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new NoSuchElementException("Incident not found: " + incidentId));

        solutionRepository.findByIncidentIdAndIsActive(incidentId, true)
                .forEach(s -> {
                    s.setIsActive(false);
                    solutionRepository.save(s);
                });

        int nextVersion = solutionRepository.findByIncidentId(incidentId).size() + 1;

        Solution solution = Solution.builder()
                .incident(incident)
                .solutionType(dto.getSolutionType() != null
                        ? Solution.SolutionType.valueOf(dto.getSolutionType())
                        : Solution.SolutionType.STEPS)
                .solutionText(dto.getSolutionText())
                .filePath(dto.getFilePath())
                .version(nextVersion)
                .createdBy(createdBy)
                .isActive(true)
                .build();

        return solutionRepository.save(solution);
    }

    @Transactional(readOnly = true)
    public List<String> getDistinctApps() {
        return incidentRepository.findDistinctApps();
    }

    @Transactional(readOnly = true)
    public long countByStatus(Incident.IncidentStatus status) {
        return incidentRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return incidentRepository.count();
    }

    @Transactional(readOnly = true)
    public long countApproved() {
        return incidentRepository.countByIsApproved(true);
    }

    @Transactional
    public void deleteIncident(Long id) {
        incidentRepository.deleteById(id);
        auditService.log(null, "DELETE_INCIDENT", "INCIDENT", id, "Deleted incident id=" + id, null);
    }
}
