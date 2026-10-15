package com.inc.chatbot.repository;

import com.inc.chatbot.entity.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    Optional<Incident> findByIncidentNo(String incidentNo);

    Optional<Incident> findByTaskNo(String taskNo);

    List<Incident> findByApp(String app);

    Page<Incident> findAll(Pageable pageable);

    Page<Incident> findByIsApproved(Boolean isApproved, Pageable pageable);

    @Query("SELECT i FROM Incident i WHERE " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.comments) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.rca) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.planSolution) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.app) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.lastComment) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Incident> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT i FROM Incident i WHERE " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.comments) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.rca) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.planSolution) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.app) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.lastComment) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Incident> searchByKeywordPaged(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT i FROM Incident i WHERE i.app = :app AND (" +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.comments) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.rca) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.planSolution) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Incident> searchByKeywordAndApp(@Param("keyword") String keyword, @Param("app") String app);

    @Query("SELECT DISTINCT i.app FROM Incident i ORDER BY i.app")
    List<String> findDistinctApps();

    long countByStatus(Incident.IncidentStatus status);

    long countByIsApproved(Boolean isApproved);
}
