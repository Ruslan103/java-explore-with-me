package ru.practicum.events.dto;

import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.StateAction;
import ru.practicum.events.model.StateEvent;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UpdateException;
import ru.practicum.user.dto.UserMapper;

public class EventMapper {
    public static EventDto toEventDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .created(event.getCreated())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(event.getInitiator())
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .published(event.getPublished())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .views(event.getViews())
                .build();
    }

    public static Event toEventFromEventDto(EventDto eventDto) {
        return Event.builder()
                .id(eventDto.getId())
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .category(eventDto.getCategory())
                .created(eventDto.getCreated())
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .initiator(eventDto.getInitiator())
                .location(eventDto.getLocation())
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .published(eventDto.getPublished())
                .requestModeration(eventDto.getRequestModeration())
                .state(eventDto.getState())
                .views(eventDto.getViews())
                .build();
    }

    public static Event toEventFromNewEventDto(NewEventDto newEventDto,
                                               CategoryRepository categoryRepository) {
        return Event.builder()
                .id(newEventDto.getId())
                .annotation(newEventDto.getAnnotation())
                .category(categoryRepository.findById(newEventDto.getCategory())
                        .orElseThrow(() -> new NotFoundException("Category not found")))
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(newEventDto.getLocation())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .build();
    }
public static Event toEventFromUpdateEvPrivate(UpdateEventUser updateEventUser,
                                               CategoryRepository categoryRepository,
                                               EventRepository eventRepository,
                                               Long eventId){
    Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event not found"));
    StateEvent stateEvent = event.getState();
//    if (stateEvent.equals(StateEvent.PENDING)) {
//        if (updateEventUser.getStateAction().equals(StateAction.REJECT_EVENT)) {
//            stateEvent = StateEvent.CANCELED;
//        }
//        if (updateEventUser.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
//            stateEvent = StateEvent.PUBLISHED;
//        }
//    } else {
//        throw new UpdateException("Invalid state");
//    }
    Long categoryId = updateEventUser.getCategory();
    Category category = null;
    if (categoryId != null) {
        category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }
    return Event.builder()
            .id(eventId)
            .annotation(updateEventUser.getAnnotation())
            .category(category)
            .description(updateEventUser.getDescription())
            .eventDate(updateEventUser.getEventDate())
            .location(updateEventUser.getLocation())
            .paid(updateEventUser.getPaid())
            .participantLimit(updateEventUser.getParticipantLimit())
            .requestModeration(updateEventUser.getRequestModeration())
            .title(updateEventUser.getTitle())
            .state(stateEvent)
            .created(event.getCreated())
            .initiator(event.getInitiator())
            .published(event.getPublished())
            .build();
}
    public static Event toEventFromUpdateEvAdmin(UpdateEventAdmin updateEventAdmin,
                                                 CategoryRepository categoryRepository,
                                                 EventRepository eventRepository,
                                                 Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        StateEvent stateEvent = event.getState();
        if (stateEvent.equals(StateEvent.PENDING)) {
            if (updateEventAdmin.getStateAction().equals(StateAction.REJECT_EVENT)) {
                stateEvent = StateEvent.CANCELED;
            }
            if (updateEventAdmin.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                stateEvent = StateEvent.PUBLISHED;
            }
        } else {
            throw new UpdateException("Invalid state");
        }
        Long categoryId = updateEventAdmin.getCategory();
        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category not found"));
        }
        return Event.builder()
                .id(eventId)
                .annotation(updateEventAdmin.getAnnotation())
                .category(category)
                .description(updateEventAdmin.getDescription())
                .eventDate(updateEventAdmin.getEventDate())
                .location(updateEventAdmin.getLocation())
                .paid(updateEventAdmin.getPaid())
                .participantLimit(updateEventAdmin.getParticipantLimit())
                .requestModeration(updateEventAdmin.getRequestModeration())
                .title(updateEventAdmin.getTitle())
                .state(stateEvent)
                .created(event.getCreated())
                .initiator(event.getInitiator())
                .published(event.getPublished())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}
