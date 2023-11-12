package ru.practicum.compilations.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@AllArgsConstructor
public class CompilationControllerAdmin {
    private final CompilationService service;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto dto) {
        return service.addCompilation(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCompile(@PathVariable Long compId) {
        service.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    CompilationDto updateCompilation(@PathVariable Long compId,
                                     @RequestBody @Valid UpdateCompilationRequest request) {
        return service.updateCompilation(compId, request);
    }
}

