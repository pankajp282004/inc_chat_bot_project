package com.inc.chatbot.repository;

import com.inc.chatbot.entity.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {

    List<Solution> findByIncidentId(Long incidentId);

    List<Solution> findByIncidentIdAndIsActive(Long incidentId, Boolean isActive);

    Optional<Solution> findTopByIncidentIdAndIsActiveOrderByVersionDesc(Long incidentId, Boolean isActive);
}
