package ru.practicum.events.service;

import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventAdmin;
import ru.practicum.events.model.StateEvent;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventDto addEvent(Long userId, NewEventDto newEventDto);

    List<EventDto> getEventsByUserId(Long userId, Integer from, Integer size);

    List<EventDto> getEventsByUserIdAndEventId(Long userId, Long eventId);

    EventDto updateEvent(Long userId, Long eventId, NewEventDto newEventDto);

    List<EventDto> getEventsAdmin(List<Long> usersId,
                                  List<StateEvent> states,
                                  List<Long> categories,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Integer from,
                                  Integer size);

    EventDto updateEventAdmin(Long eventId, UpdateEventAdmin updateEventAdmin);

    List<EventShortDto> getEventsPublic(String text,
                                        List<Long> categories,
                                        boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        boolean onlyAvailable,
                                        String sort,
                                        Integer from,
                                        Integer size,
                                        HttpServletRequest request);

    public EventDto getEventByIdPublic(Long id, HttpServletRequest request);


}
