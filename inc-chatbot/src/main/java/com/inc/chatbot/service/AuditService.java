package com.inc.chatbot.service;

import com.inc.chatbot.entity.AuditLog;
import com.inc.chatbot.repository.AuditLogRepository;
import com.inc.chatbot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void log(String username, String action, String entityType,
                    Long entityId, String description, String ipAddress) {
        try {
            AuditLog.Builder builder = AuditLog.builder()
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .description(description)
                    .ipAddress(ipAddress);

            if (username != null) {
                userRepository.findByUsername(username).ifPresent(builder::user);
            }

            auditLogRepository.save(builder.build());
        } catch (Exception e) {
            log.error("Failed to write audit log: {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getLogsForIncident(Long incidentId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc("INCIDENT", incidentId);
    }
}
