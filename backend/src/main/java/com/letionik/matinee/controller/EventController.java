package com.letionik.matinee.controller;

import com.letionik.matinee.CreateEventRequestDto;
import com.letionik.matinee.EventDto;
import com.letionik.matinee.TaskProgressDto;
import com.letionik.matinee.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by Alexey Zyabkin on 12.12.2015.
 */
@RestController
@RequestMapping(value = "event")
public class EventController {
    @Autowired
    private EventService eventService;

    @RequestMapping(value = "/{eventId}", method = RequestMethod.GET)
    public EventDto getCurrentEvent(@PathVariable Long eventId) {
        return eventService.getEventInfo(eventId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public EventDto createEvent(@RequestBody CreateEventRequestDto createEventRequest, HttpSession session) {
        return eventService.createEvent(createEventRequest, (Long) session.getAttribute("user"));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/enroll/{code}")
    public EventDto enroll(@PathVariable String code, HttpSession session) {
        return eventService.enroll(code, (Long) session.getAttribute("user"));
    }

    @RequestMapping(value = "/reveal/tasks/{eventId}", method = RequestMethod.POST)
    public EventDto revealTasks(@PathVariable Long eventId) {
        return eventService.revealTasks(eventId);
    }

    @RequestMapping(value = "/reveal/roles/{eventId}", method = RequestMethod.POST)
    public EventDto revealRoles(@PathVariable Long eventId) {
        return eventService.revealRoles(eventId);
    }

    @RequestMapping(value = "history/{eventId}", method = RequestMethod.GET)
    public List<TaskProgressDto> getHistory(@PathVariable Long eventId) {
        return eventService.getHistory(eventId);
    }
}