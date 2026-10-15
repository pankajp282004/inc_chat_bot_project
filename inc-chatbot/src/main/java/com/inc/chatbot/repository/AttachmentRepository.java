package com.inc.chatbot.repository;

import com.inc.chatbot.entity.IncidentAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<IncidentAttachment, Long> {

    List<IncidentAttachment> findByIncidentId(Long incidentId);

    void deleteByIncidentId(Long incidentId);
}
