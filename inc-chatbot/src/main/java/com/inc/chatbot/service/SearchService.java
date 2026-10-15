package com.inc.chatbot.service;

import com.inc.chatbot.dto.SearchRequest;
import com.inc.chatbot.dto.SearchResponse;
import com.inc.chatbot.entity.Incident;
import com.inc.chatbot.entity.Solution;
import com.inc.chatbot.repository.IncidentRepository;
import com.inc.chatbot.repository.SolutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final IncidentRepository incidentRepository;
    private final SolutionRepository solutionRepository;

    public SearchService(IncidentRepository incidentRepository,
                         SolutionRepository solutionRepository) {
        this.incidentRepository = incidentRepository;
        this.solutionRepository = solutionRepository;
    }

    public SearchResponse search(SearchRequest request) {
        String query = request.getQuery();
        String app = request.getApp();

        if (!StringUtils.hasText(query)) {
            return SearchResponse.builder()
                    .query(query)
                    .totalResults(0)
                    .results(Collections.emptyList())
                    .hasMore(false)
                    .build();
        }

        List<SearchResponse.IncidentSearchResult> results = new ArrayList<>();

        // Level 1: Exact Match by incident_no or task_no
        List<SearchResponse.IncidentSearchResult> exactMatches = findExactMatches(query.trim());
        results.addAll(exactMatches);

        // Level 2: Keyword Search across text fields
        List<SearchResponse.IncidentSearchResult> keywordMatches = findKeywordMatches(query.trim(), app);

        // Merge - avoid duplicates from exact match
        Set<Long> exactIds = exactMatches.stream()
                .map(SearchResponse.IncidentSearchResult::getId)
                .collect(Collectors.toSet());

        keywordMatches.stream()
                .filter(r -> !exactIds.contains(r.getId()))
                .forEach(results::add);

        // Sort: exact first (score 100), then by score desc
        results.sort(Comparator.comparingInt(SearchResponse.IncidentSearchResult::getMatchScore).reversed());

        // Apply status filter if provided
        if (StringUtils.hasText(request.getStatus())) {
            String statusFilter = request.getStatus().toUpperCase();
            results = results.stream()
                    .filter(r -> statusFilter.equals(r.getStatus()))
                    .collect(Collectors.toList());
        }

        int total = results.size();
        int page = Math.max(0, request.getPage());
        int size = request.getSize() > 0 ? request.getSize() : 10;
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, total);

        List<SearchResponse.IncidentSearchResult> paged = fromIndex < total
                ? results.subList(fromIndex, toIndex)
                : Collections.emptyList();

        return SearchResponse.builder()
                .query(query)
                .totalResults(total)
                .results(paged)
                .hasMore(toIndex < total)
                .build();
    }

    private List<SearchResponse.IncidentSearchResult> findExactMatches(String query) {
        List<SearchResponse.IncidentSearchResult> results = new ArrayList<>();

        incidentRepository.findByIncidentNo(query).ifPresent(incident ->
                results.add(mapToResult(incident, "EXACT", 100)));

        incidentRepository.findByTaskNo(query).ifPresent(incident -> {
            if (results.stream().noneMatch(r -> r.getId().equals(incident.getId()))) {
                results.add(mapToResult(incident, "EXACT", 100));
            }
        });

        return results;
    }

    private List<SearchResponse.IncidentSearchResult> findKeywordMatches(String query, String app) {
        List<Incident> incidents;

        if (StringUtils.hasText(app)) {
            incidents = incidentRepository.searchByKeywordAndApp(query, app);
        } else {
            String[] tokens = query.split("\\s+");
            Set<Long> seenIds = new HashSet<>();
            List<Incident> merged = new ArrayList<>();

            for (String token : tokens) {
                if (token.length() < 2) continue;
                incidentRepository.searchByKeyword(token).forEach(inc -> {
                    if (seenIds.add(inc.getId())) {
                        merged.add(inc);
                    }
                });
            }
            incidents = merged;
        }

        return incidents.stream()
                .map(inc -> {
                    int score = calculateKeywordScore(inc, query);
                    return mapToResult(inc, "KEYWORD", score);
                })
                .collect(Collectors.toList());
    }

    private int calculateKeywordScore(Incident incident, String query) {
        String lowerQuery = query.toLowerCase();
        String[] tokens = lowerQuery.split("\\s+");
        int score = 0;

        for (String token : tokens) {
            if (token.length() < 2) continue;
            if (incident.getDescription() != null && incident.getDescription().toLowerCase().contains(token)) score += 20;
            if (incident.getRca() != null && incident.getRca().toLowerCase().contains(token)) score += 15;
            if (incident.getPlanSolution() != null && incident.getPlanSolution().toLowerCase().contains(token)) score += 15;
            if (incident.getComments() != null && incident.getComments().toLowerCase().contains(token)) score += 10;
            if (incident.getApp() != null && incident.getApp().toLowerCase().contains(token)) score += 5;
            if (incident.getLastComment() != null && incident.getLastComment().toLowerCase().contains(token)) score += 5;
        }

        return Math.min(score, 99);
    }

    private SearchResponse.IncidentSearchResult mapToResult(Incident incident, String matchReason, int matchScore) {
        String solutionText = solutionRepository
                .findTopByIncidentIdAndIsActiveOrderByVersionDesc(incident.getId(), true)
                .map(Solution::getSolutionText)
                .orElse(incident.getPlanSolution());

        return SearchResponse.IncidentSearchResult.builder()
                .id(incident.getId())
                .incidentNo(incident.getIncidentNo())
                .taskNo(incident.getTaskNo())
                .app(incident.getApp())
                .description(incident.getDescription())
                .status(incident.getStatus() != null ? incident.getStatus().name() : null)
                .rca(incident.getRca())
                .planSolution(incident.getPlanSolution())
                .solution(solutionText)
                .matchReason(matchReason)
                .matchScore(matchScore)
                .createdAt(incident.getCreatedAt())
                .isApproved(incident.getIsApproved())
                .build(); // uses IncidentSearchResult.builder() -> IBuilder
    }
}
