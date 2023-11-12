package ru.practicum.compilations.service;


import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationMapper;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository repository;
    private final EventRepository eventRepository;

    public CompilationDto addCompilation(NewCompilationDto dto) {
        Compilation compilation = CompilationMapper.toEntity(dto);
        if (dto.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(dto.getEvents());
            compilation.setEvents(events);
        }
        if (dto.getPinned() == null) {
            compilation.setPinned(false);
        }
        return CompilationMapper.toDto(repository.save(compilation));
    }

    @Override
    public void deleteCompilation(Long compId) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка с id=" + compId + " не найдена"));
        repository.delete(compilation);

    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка с id=" + compId + " не найдена"));
        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }
        if (request.getTitle() != null) {
            compilation.setTitle(request.getTitle());
        }
        if (request.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(request.getEvents());
            compilation.setEvents(events);
        }
        return CompilationMapper.toDto(repository.save(compilation));
    }

    @Override
    public Collection<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {

        PageRequest page = PageRequest.of(from / size, size);
        if (pinned == null) {
            return repository.findAll(page)
                    .stream()
                    .map(CompilationMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return repository.findAllByPinned(pinned, page)
                    .stream()
                    .map(CompilationMapper::toDto)
                    .collect(Collectors.toList());

        }
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        return CompilationMapper.toDto(repository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка с id=" + compId + " не найдена")));
    }
}
