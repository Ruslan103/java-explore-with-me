package ru.practicum.category.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/categories")
@Validated
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    public void deleteCategory(@Positive @PathVariable Long catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@Positive @PathVariable Long catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        return categoryService.updateCategory(catId, categoryDto);
    }
}
