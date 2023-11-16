package ru.practicum.events.service;

import ru.practicum.events.dto.*;
import ru.practicum.events.model.StateEvent;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventDto addEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);

    EventDto getEventById(Long userId, Long eventId);

    EventDto updateEvent(Long userId, Long eventId, UpdateEventUser updateEvent);

    List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request);

    EventDto getEventByIdPublic(Long id, HttpServletRequest request);

    List<EventDto> getEventsAdmin(List<Long> users, List<StateEvent> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventDto updateEventAdmin(Long eventId, UpdateEventAdmin updateEventAdmin);
}
