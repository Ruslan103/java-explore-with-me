package ru.practicum.request.service;


import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.Collection;
import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequestsById(Long userId);

    ParticipationRequestDto addRequest(Long userId, Long eventId);

    ParticipationRequestDto updateToCancel(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request);
}

