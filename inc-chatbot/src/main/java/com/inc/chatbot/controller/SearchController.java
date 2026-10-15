package com.inc.chatbot.controller;

import com.inc.chatbot.dto.SearchRequest;
import com.inc.chatbot.dto.SearchResponse;
import com.inc.chatbot.service.IncidentService;
import com.inc.chatbot.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;
    private final IncidentService incidentService;

    public SearchController(SearchService searchService, IncidentService incidentService) {
        this.searchService = searchService;
        this.incidentService = incidentService;
    }

    @GetMapping
    public String searchPage(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String app,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        model.addAttribute("apps", incidentService.getDistinctApps());
        model.addAttribute("query", q);
        model.addAttribute("selectedApp", app);
        model.addAttribute("selectedStatus", status);

        if (q != null && !q.isBlank()) {
            SearchRequest request = SearchRequest.builder()
                    .query(q)
                    .app(app)
                    .status(status)
                    .page(page)
                    .size(20)
                    .build();

            SearchResponse response = searchService.search(request);
            model.addAttribute("searchResponse", response);
            model.addAttribute("results", response.getResults());
            model.addAttribute("totalResults", response.getTotalResults());
        }

        return "search-results";
    }
}
