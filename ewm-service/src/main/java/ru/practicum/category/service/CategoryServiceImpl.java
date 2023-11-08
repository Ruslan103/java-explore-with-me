package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import javax.validation.ValidationException;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    public void deleteCategory(long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с Id=" + id + " не найдена!"));
        if (eventRepository.findEventByCategoryId(id)) {
            categoryRepository.deleteById(id);
        } else {
            throw new ValidationException("Категория не может быть удалена, т.к. к ней привязаны события.");
        }

    }

    public CategoryDto updateCategory(long id, CategoryDto categoryDto) {

        Category category = CategoryMapper.toCategory(categoryDto);
        category.setId(id);
        if (categoryRepository.existsById(id)) {
            return CategoryMapper.toCategoryDto(categoryRepository.save(category));
        } else {
            throw new NotFoundException("Категория с Id=" + id + " не найдена!");
        }
    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        return categoryRepository.findAll(page)
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }

    public CategoryDto getCategoryById(Long id) {
        if (categoryRepository.existsById(id)) {
            return CategoryMapper.toCategoryDto(categoryRepository.getReferenceById(id));
        } else {
            throw new NotFoundException("Категория с Id=" + id + " не найдена!");
        }
    }
}
