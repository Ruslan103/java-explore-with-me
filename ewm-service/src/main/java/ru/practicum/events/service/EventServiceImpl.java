package ru.practicum.events.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.StateEvent;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.DateException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.events.model.StateEvent.PUBLISHED;

@Service
@AllArgsConstructor
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StatsClient statsClient;

    public EventDto addEvent(Long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Event event = EventMapper.toEventFromNewEventDto(newEventDto, categoryRepository);
        event.setInitiator(initiator);
        event.setState(StateEvent.PENDING);
        return EventMapper.toEventDto(eventRepository.save(event));
    }

    // GET/users/{userId}/events
    public List<EventDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        return eventRepository.findEventByInitiator(userId, page).stream()
                .map(EventMapper::toEventDto)
                .collect(Collectors.toList());
    }


    // GET/users/{userId}/events/{eventId}
    public List<EventDto> getEventsByUserIdAndEventId(Long userId, Long eventId) {
        if (eventRepository.existsById(eventId)) {
            return eventRepository.findEventsByIdAndInitiator(eventId, userId).stream()
                    .map(EventMapper::toEventDto)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Event not found");
        }
    }

    // PATCH /users/{userId}/events/{eventId}
    public EventDto updateEvent(Long userId, Long eventId, NewEventDto newEventDto) {
        Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (!oldEvent.getState().equals(PUBLISHED)) {
            throw new NotFoundException("Event state is published");
        }
        Event updateEvent = EventMapper.toEventFromNewEventDto(newEventDto, categoryRepository);
        updateEvent.setCreated(oldEvent.getCreated());
        updateEvent.setInitiator(oldEvent.getInitiator());
        updateEvent.setPublished(oldEvent.getPublished());
        updateEvent.setState(oldEvent.getState());
        return EventMapper.toEventDto(updateEvent);
    }

    // GET/admin/events
    public List<EventDto> getEventsAdmin(List<Long> usersId,
                                         List<StateEvent> states,
                                         List<Long> categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Integer from,
                                         Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        return eventRepository.getEventsAdmin(usersId, states, categories, rangeStart, rangeEnd, page).stream()
                .map(EventMapper::toEventDto)
                .collect(Collectors.toList());

    }

    //PATCH /admin/events/{eventId}
    public EventDto updateEventAdmin(Long eventId, UpdateEventAdmin updateEventAdmin) {
        Event event = EventMapper.toEventFromUpdateEvAdmin(updateEventAdmin,
                categoryRepository,
                eventRepository,
                eventId);
        return EventMapper.toEventDto(eventRepository.save(event));
    }

    // GET //events
    public List<EventShortDto> getEventsPublic(String text,
                                               List<Long> categories,
                                               boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               boolean onlyAvailable,
                                               String sort,
                                               Integer from,
                                               Integer size,
                                               HttpServletRequest request) {
        PageRequest page = PageRequest.of(from / size, size);

        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart))
                throw new DateException("Неверно указано время");
        }
        statsClient.addStats(EndpointHitDto.builder() // добавление в статистику
                .uri(request.getRequestURI())
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        List<Event> events = eventRepository.getEventsPublic(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                page);
        if (sort.equals("EVENT_DATE")) {
            events.sort(Comparator.comparing(Event::getEventDate));
        }
        if (sort.equals("VIEWS")) {
            events.sort(Comparator.comparing(Event::getViews).reversed());
        }
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public EventDto getEventByIdPublic(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event id=" + id + " not found!"));
        if (!event.getState().equals(PUBLISHED)) {
            throw new EntityNotFoundException("Event id=" + id + " not found!");
        }
        event.setViews(eventViews(event));
        statsClient.addStats(EndpointHitDto.builder() // добавление в статистику
                .uri(request.getRequestURI())
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
        return EventMapper.toEventDto(event);
    }


    private long eventViews(Event event) {
        long views;
        List<String> uri = List.of("/events/" + event.getId());
        List<ViewStatsDto> viewStats = statsClient.getStats(event.getCreated(),
                LocalDateTime.now(), uri, true);
        if (viewStats.isEmpty()) {
            return 0;
        } else {
            views = viewStats.get(0).getHits();
        }
        return views;
    }


}
