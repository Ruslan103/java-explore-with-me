package ru.practicum.request.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.StateEvent;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UpdateException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestMapper;
import ru.practicum.request.model.EventRequestStatus;
import ru.practicum.request.model.Request;
import ru.practicum.request.reposytory.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public List<ParticipationRequestDto> getRequestsById(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Collection<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event  not found!"));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getState().equals(StateEvent.PUBLISHED)) {
            throw new ValidationException("Нельзя участвовать в неопубликованном событии");
        }
        Integer confirmedRequest = requestRepository.getConfirmedRequestsByEventId(eventId);
        if (confirmedRequest.equals(event.getParticipantLimit()) && event.getParticipantLimit() != 0) {
            throw new ValidationException("У события достигнут лимит запросов на участие");
        }
        Request newRequest = Request.builder()
                .requester(user)
                .event(event)
                .build();
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new UpdateException("Request exist");
        }
        if (event.getRequestModeration() && event.getParticipantLimit() > 0) {
            newRequest.setState(EventRequestStatus.PENDING);
        } else {
            newRequest.setState(EventRequestStatus.CONFIRMED);
            eventRepository.save(event);
        }
        return RequestMapper.toDto(requestRepository.save(newRequest));
    }

    public ParticipationRequestDto updateToCancel(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Request requestToUpdate = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        if (requestToUpdate.getRequester().getId().equals(userId)) {
            requestToUpdate.setState(EventRequestStatus.CANCELED);
        } else {
            throw new ValidationException("У пользователя с id=" + userId + " нет доступа к запросу");
        }
        return RequestMapper.toDto(requestRepository.save(requestToUpdate));
    }

    public List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found!"));
        if (event.getInitiator().getId().equals(userId)) {
            return requestRepository.findAllByEventId(eventId)
                    .stream()
                    .map(RequestMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }

    }

    public EventRequestStatusUpdateResult updateEventRequestStatus(Long userId,
                                                                   Long eventId,
                                                                   EventRequestStatusUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));


        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ValidationException("Request incorrect");
        }
        Integer confirmedRequest = requestRepository.getConfirmedRequestsByEventId(eventId);
        if (confirmedRequest >= event.getParticipantLimit()) {
            throw new ValidationException("Limit request");
        }
        List<Request> requests = requestRepository.findAllByIdIn(request.getRequestIds());
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        switch (request.getStatus()) {
            case CONFIRMED:
                int confirmationLimit = event.getParticipantLimit() - confirmedRequest;
                for (Request request1 : requests) {
                    if (!request1.getState().equals(EventRequestStatus.PENDING)) {
                        throw new ValidationException("Status incorrect");
                    }
                    if (confirmationLimit != 0) {
                        request1.setState(EventRequestStatus.CONFIRMED);
                        confirmationLimit--;
                    } else {
                        request1.setState(EventRequestStatus.REJECTED);
                    }
                }
                break;
            case REJECTED:
                requests.forEach(request1 -> request1.setState(EventRequestStatus.REJECTED));
                requestRepository.saveAll(requests);
        }
        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();
        for (Request request1 : requests) {
            if (request1.getState().equals(EventRequestStatus.CONFIRMED)) {
                confirmed.add(RequestMapper.toDto(request1));
            } else if (request1.getState().equals(EventRequestStatus.REJECTED)) {
                rejected.add(RequestMapper.toDto(request1));
            }
        }
        result.setRejectedRequests(rejected);
        result.setConfirmedRequests(confirmed);
        eventRepository.save(event);
        return result;
    }
}

