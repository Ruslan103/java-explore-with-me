package ru.practicum.compilations.dto;

import ru.practicum.compilations.model.Compilation;
import ru.practicum.events.dto.EventMapper;

import java.util.ArrayList;
import java.util.stream.Collectors;


public class CompilationMapper {

    public static Compilation toEntity(NewCompilationDto dto) {
        return Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.getPinned())
                .build();

    }

    public static Compilation toEntity(CompilationDto dto) {
        return Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.getPinned())
                .build();

    }


    public static CompilationDto toDto(Compilation model) {
        CompilationDto result = CompilationDto.builder()
                .id(model.getId())
                .pinned(model.getPinned())
                .title(model.getTitle())
                .build();
        if (model.getEvents() != null) {
            result.setEvents(model.getEvents()
                    .stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList()));
        } else {
            result.setEvents(new ArrayList<>());
        }
        return result;
    }
}
