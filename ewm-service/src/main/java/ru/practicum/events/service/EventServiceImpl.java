package ru.practicum.events.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.category.service.CategoryService;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.Location;
import ru.practicum.events.model.StateAction;
import ru.practicum.events.model.StateEvent;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.repository.LocationRepository;
import ru.practicum.exception.DateException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UpdateException;
import ru.practicum.request.reposytory.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.events.model.StateEvent.PUBLISHED;


@Service
@Slf4j
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryService categoryService;
    private final StatsClient statsClient;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    public EventDto addEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DateException("Date incorrect");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));
        Location location = locationRepository.save(newEventDto.getLocation());
        Event eventToSave = EventMapper.toEvent(newEventDto);
        eventToSave.setInitiator(user);
        eventToSave.setCategory(category);
        eventToSave.setLocation(location);
        eventToSave.setState(StateEvent.PENDING);
        if (eventToSave.getPaid() == null) {
            eventToSave.setPaid(false);
        }
        if (eventToSave.getParticipantLimit() == null) {
            eventToSave.setParticipantLimit(0);
        }
        if (eventToSave.getRequestModeration() == null) {
            eventToSave.setRequestModeration(true);
        }
        return EventMapper.toEventDto(eventRepository.save(eventToSave));
    }

    public List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));
        PageRequest page = PageRequest.of(from / size, size);
        List<EventShortDto> events = eventRepository.getEventsByUser(userId, page).stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
        List<Integer> requestsCount = requestRepository.getConfirmedRequestsByListOfEvents(events
                .stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList()));

        for (int i = 0; i < requestsCount.size(); i++) {
            events.get(i).setConfirmedRequests(requestsCount.get(i));
        }
        return events;
    }

    private static void setConfirmedRequests(List<EventShortDto> list, RequestRepository requestRepository) {
        List<Integer> requestsCount = requestRepository.getConfirmedRequestsByListOfEvents(list
                .stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList()));

        for (int i = 0; i < requestsCount.size(); i++) {
            list.get(i).setConfirmedRequests(requestsCount.get(i));
        }
    }

    public EventDto getEventById(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found!"));
        EventDto result = EventMapper.toEventDto(event);
        result.setConfirmedRequests(requestRepository.getConfirmedRequestsByEventId(eventId));
        return result;
    }

    public EventDto updateEvent(Long userId, Long eventId, UpdateEventUser updateEvent) {
        Event eventToUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found!"));
        if (eventToUpdate.getState() == PUBLISHED) {
            throw new UpdateException("State invalid");
        } else {
            if (updateEvent.getAnnotation() != null) {
                eventToUpdate.setAnnotation(updateEvent.getAnnotation());
            }
            if (updateEvent.getCategory() != null) {
                Category category = CategoryMapper.toCategory(categoryService.getCategoryById(updateEvent.getCategory()));
                eventToUpdate.setCategory(category);
            }
            if (updateEvent.getDescription() != null) {
                eventToUpdate.setDescription(updateEvent.getDescription());
            }
            if (updateEvent.getEventDate() != null) {
                if (updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                    throw new DateException("Date incorrect");
                }
                eventToUpdate.setEventDate(updateEvent.getEventDate());
            }
            if (updateEvent.getPaid() != null) {
                eventToUpdate.setPaid(updateEvent.getPaid());
            }
            if (updateEvent.getParticipantLimit() != null) {
                eventToUpdate.setParticipantLimit(updateEvent.getParticipantLimit());
            }
            if (updateEvent.getRequestModeration() != null) {
                eventToUpdate.setRequestModeration(updateEvent.getRequestModeration());
            }
            if (updateEvent.getTitle() != null) {
                eventToUpdate.setTitle(updateEvent.getTitle());
            }
            if (updateEvent.getStateAction() != null) {
                if (updateEvent.getStateAction() == StateAction.CANCEL_REVIEW) {
                    eventToUpdate.setState(StateEvent.CANCELED);
                }
                if (updateEvent.getStateAction() == StateAction.SEND_TO_REVIEW) {
                    eventToUpdate.setState(StateEvent.PENDING);
                }
            }
            if (updateEvent.getLocation() != null) {
                Location location = locationRepository.save(updateEvent.getLocation());
                eventToUpdate.setLocation(location);
            }
            eventRepository.save(eventToUpdate);
            EventDto eventDto = EventMapper.toEventDto(eventToUpdate);
            eventDto.setConfirmedRequests(requestRepository.getConfirmedRequestsByEventId(eventDto.getId()));
            return eventDto;
        }
    }

    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, Integer from,
                                               Integer size, HttpServletRequest request) {
        PageRequest page = PageRequest.of(from / size, size, Sort.unsorted());
        if (text == null && categories == null && paid == null && rangeStart == null && rangeEnd == null) {
            saveStats(request);
            return Collections.emptyList();
        }
        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart))
                throw new DateException("Конфликт временной выборки");
        }
        List<Event> events;
        if (sort.equals("EVENT_DATE")) {
            page = PageRequest.of(from / size, size, Sort.by("eventDate"));

        }
        events = eventRepository.findAllPublishState(rangeStart, rangeEnd, categories,
                paid, text, page);
        if (onlyAvailable) {
            events = events.stream()
                    .filter(event -> !requestRepository.getConfirmedRequestsByEventId(event.getId()).equals(event.getParticipantLimit())).collect(Collectors.toList());
        }
        views(events, statsClient);  // добавил просмотры
        saveStats(request);     // сохранил запрос в сервер статистики
        List<EventShortDto> result = events.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
        if (sort.equals("VIEWS")) {
            return result.stream()
                    .sorted(Comparator.comparingLong(EventShortDto::getViews).reversed())
                    .collect(Collectors.toList());
        }
        setConfirmedRequests(result, requestRepository);
        return result;
    }

    public EventDto getEventByIdPublic(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found!"));
        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException("Event not found!");
        }
        event.setViews(setViewsInEvent(event));
        saveStats(request);
        EventDto result = EventMapper.toEventDto(event);
        result.setConfirmedRequests(requestRepository.getConfirmedRequestsByEventId(id));
        return result;
    }

    public List<EventDto> getEventsAdmin(List<Long> users,
                                         List<StateEvent> states,
                                         List<Long> categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Integer from,
                                         Integer size) {

        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new DateException("Date incorrect");
            }
        }
        PageRequest page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllAdmin(users, states, categories, rangeStart, rangeEnd, page);
        List<EventDto> result = events.stream()
                .map(EventMapper::toEventDto)
                .collect(Collectors.toList());
        List<Integer> requestsCount = requestRepository.getConfirmedRequestsByListOfEvents(result
                .stream()
                .map(EventDto::getId)
                .collect(Collectors.toList()));
        for (int i = 0; i < requestsCount.size(); i++) {
            result.get(i).setConfirmedRequests(requestsCount.get(i));
        }
        return result;
    }

    public EventDto updateEventAdmin(Long eventId, UpdateEventAdmin updateEventAdmin) {
        Event newEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found!"));
        if (newEvent.getState() == PUBLISHED) {
            throw new UpdateException("State incorrect");
        }
        if (updateEventAdmin.getAnnotation() != null) {
            newEvent.setAnnotation(updateEventAdmin.getAnnotation());
        }
        if (updateEventAdmin.getDescription() != null) {
            newEvent.setDescription(updateEventAdmin.getDescription());
        }
        if (updateEventAdmin.getCategory() != null) {
            Category category = CategoryMapper.toCategory(categoryService.getCategoryById(updateEventAdmin.getCategory()));
            newEvent.setCategory(category);
        }
        if (updateEventAdmin.getPaid() != null) {
            newEvent.setPaid(updateEventAdmin.getPaid());
        }
        if (updateEventAdmin.getParticipantLimit() != null) {
            newEvent.setParticipantLimit(updateEventAdmin.getParticipantLimit());
        }
        if (updateEventAdmin.getRequestModeration() != null) {
            newEvent.setRequestModeration(updateEventAdmin.getRequestModeration());
        }
        if (updateEventAdmin.getTitle() != null) {
            newEvent.setTitle(updateEventAdmin.getTitle());
        }
        if (updateEventAdmin.getStateAction() != null) {
            if (newEvent.getState().equals(StateEvent.PENDING)) {
                if (updateEventAdmin.getStateAction().equals(StateAction.REJECT_EVENT)) {
                    newEvent.setState(StateEvent.CANCELED);
                }
                if (updateEventAdmin.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                    newEvent.setState(PUBLISHED);
                    newEvent.setPublished(LocalDateTime.now());
                }
            } else {
                throw new UpdateException("State incorrect");
            }
        }
        if (updateEventAdmin.getLocation() != null) {
            Location location = locationRepository.save(updateEventAdmin.getLocation());
            newEvent.setLocation(location);
        }
        if (updateEventAdmin.getEventDate() != null && newEvent.getState().equals(PUBLISHED)) {
            if (updateEventAdmin.getEventDate().isAfter(newEvent.getPublished().plusHours(1))) {
                newEvent.setEventDate(updateEventAdmin.getEventDate());
            } else {
                throw new DateException("Date incorrect");
            }
        }
        eventRepository.save(newEvent);
        EventDto result = EventMapper.toEventDto(newEvent);
        result.setConfirmedRequests(requestRepository.getConfirmedRequestsByEventId(result.getId()));
        return result;
    }

    private long setViewsInEvent(Event event) {
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

    private static void views(List<Event> events, StatsClient statsClient) {
        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        LocalDateTime startDate = events
                .stream()
                .sorted(Comparator.comparing(Event::getPublished))
                .collect(Collectors.toList())
                .get(0)
                .getPublished();

        List<ViewStatsDto> stats = statsClient.getStats(startDate, LocalDateTime.now(), uris, true);

        Map<Long, Long> eventViews = new HashMap<>();

        for (ViewStatsDto viewStatsDto : stats) {
            String uri = viewStatsDto.getUri();
            String[] split = uri.split("/");
            String id = split[2];
            Long eventId = Long.parseLong(id);
            eventViews.put(eventId, viewStatsDto.getHits());
        }
        events.forEach(event -> event.setViews(eventViews.getOrDefault(event.getId(), 0L)));
    }

    private void saveStats(HttpServletRequest request) {
        statsClient.addStats(EndpointHitDto.builder()
                .uri(request.getRequestURI())
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }
}

