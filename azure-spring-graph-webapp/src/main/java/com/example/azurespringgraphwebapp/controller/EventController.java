package com.example.azurespringgraphwebapp.controller;

import com.example.azurespringgraphwebapp.dto.EventDto;
import com.example.azurespringgraphwebapp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller("/")
public class EventController {
    @Autowired
    private EventService eventService;

    private EventDto currentEvent = new EventDto();

    @GetMapping("/events")
    public ModelAndView getEvents(Model model, @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphAuthorizedClient) {
        ModelAndView modelAndView = new ModelAndView("events");
        model.addAttribute("newEvent", new EventDto());
        model.addAttribute("selectedEvent", currentEvent);
        model.addAttribute("eventId", new String());
        model.addAttribute("events", eventService.getEvents(graphAuthorizedClient));
        model.addAttribute("calendars", eventService.getCalendars(graphAuthorizedClient));
        return modelAndView;
    }

    @GetMapping(path = "/getEvent")
    public String getEvent(@RequestParam("eventId") String eventId, Model model, @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphAuthorizedClient) {
        currentEvent = eventService.getEvent(eventId, graphAuthorizedClient);
        model.addAttribute("selectedEvent", currentEvent);
        return "redirect:/events";
    }

    @PostMapping(path = "/create")
    public String addEvent(@ModelAttribute("newEvent") EventDto event, @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphAuthorizedClient) {
        eventService.createEvent(event, graphAuthorizedClient);
        return "redirect:/events";
    }

    @PostMapping(path = "/eventAction", params = "action=delete")
    public String removeEvent(@ModelAttribute(name="currentEvent") EventDto event, @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphAuthorizedClient) {
        eventService.deleteEvent(event, graphAuthorizedClient);
        return "redirect:/events";
    }

    @PostMapping(path = "/eventAction", params = "action=update")
    public String updateEvent(@ModelAttribute(name="currentEvent") EventDto event, @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphAuthorizedClient) {
        eventService.updateEvent(event, graphAuthorizedClient);
        return "redirect:/events";
    }
}
