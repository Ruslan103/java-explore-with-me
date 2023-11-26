package ru.practicum.compilations.controller;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@AllArgsConstructor
public class CompilationPublicController {
    private final CompilationService service;

    @GetMapping
    List<CompilationDto> getCompilations(@RequestParam(required = false) boolean pinned,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(defaultValue = "10") @Positive Integer size) {
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    CompilationDto getCompilationById(@PathVariable Long compId) {
        return service.getCompilationById(compId);
    }
}
