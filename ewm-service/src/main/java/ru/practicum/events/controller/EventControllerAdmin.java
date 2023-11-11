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
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/events")
@Validated
public class EventControllerAdmin {

    private final EventService eventService;

    @GetMapping
    public List<EventDto> getEventsAdmin(@RequestParam(required = false) List<Long> usersId,
                                         @RequestParam(required = false) List<StateEvent> states,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(defaultValue = "10") @PositiveOrZero Integer size) {
        return eventService.getEventsAdmin(usersId, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping ("/{eventId}")
    public EventDto updateEventAdmin(@Positive @PathVariable Long eventId, @Valid @RequestBody UpdateEventAdmin updateEventAdmin) {
        return eventService.updateEventAdmin(eventId, updateEventAdmin);
    }

}
