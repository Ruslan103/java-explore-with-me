package ru.practicum.events.controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.UpdateEventAdmin;
import ru.practicum.events.model.StateEvent;
import ru.practicum.events.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/events")
@Validated
public class EventControllerAdmin {

    private final EventService service;

    @GetMapping
    public Collection<EventDto> getEvents(@RequestParam(required = false) List<Long> users,
                                          @RequestParam(required = false) List<StateEvent> states,
                                          @RequestParam(required = false) List<Long> categories,
                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        return service.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateAdmin(@PathVariable Long eventId,
                                @RequestBody @Valid UpdateEventAdmin updateEventAdmin) {
        return service.updateEventAdmin(eventId, updateEventAdmin);
    }

}
