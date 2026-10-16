package com.inc.chatbot.controller;

import com.inc.chatbot.service.IncidentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ChatController {

    private final IncidentService incidentService;

    public ChatController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/chat";
    }

    @GetMapping("/chat")
    public String chatPage(Model model) {
        List<String> apps = incidentService.getDistinctApps();
        model.addAttribute("apps", apps);
        model.addAttribute("totalIncidents", incidentService.countAll());
        model.addAttribute("approvedIncidents", incidentService.countApproved());
        return "chat";
    }
}
