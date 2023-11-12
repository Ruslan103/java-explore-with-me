package ru.practicum.events.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventUser;
import ru.practicum.events.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
@Validated
public class EventControllerPrivate {
    private final EventService service;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    EventDto createEvent(@RequestBody @Valid NewEventDto newEventDto,
                         @PathVariable Long userId) {
        return service.addEvent(userId, newEventDto);
    }

    @GetMapping
    Collection<EventDto> getEventsByUserId(@PathVariable Long userId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {

        return service.getEventsByUserId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    EventDto getEventByUserIdAndEventId(@PathVariable Long userId,
                                        @PathVariable Long eventId) {
        return service.getEventByUserIdAndEventId(userId, eventId);
    }


    @PatchMapping("/{eventId}")
    EventDto updateEvent(@PathVariable Long userId,
                         @PathVariable Long eventId,
                         @RequestBody @Valid UpdateEventUser updateEventUser) {
        return service.updateEvent(userId, eventId, updateEventUser);
    }

    @GetMapping("/{eventId}/requests")
    Collection<ParticipationRequestDto> getRequestsForEvent(@PathVariable Long userId,
                                                            @PathVariable Long eventId) {
        return requestService.getRequestsForEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    EventRequestStatusUpdateResult updateEventRequestStatus(@PathVariable Long userId,
                                                            @PathVariable Long eventId,
                                                            @RequestBody @Valid EventRequestStatusUpdateRequest request) {
        return requestService.updateEventRequestStatus(userId, eventId, request);
    }
}
