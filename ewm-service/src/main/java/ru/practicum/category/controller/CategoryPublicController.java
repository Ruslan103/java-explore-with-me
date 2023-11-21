package ru.practicum.category.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/categories")
@Validated
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    List<CategoryDto> getAll(@RequestParam(value = "from", defaultValue = "0")
                             @PositiveOrZero Integer from,
                             @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    CategoryDto getById(@PathVariable Long catId) {
        return categoryService.getCategoryById(catId);
    }
}