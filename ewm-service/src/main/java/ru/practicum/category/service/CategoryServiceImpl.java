package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UpdateException;
import ru.practicum.exception.ValidationException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;


    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        if (categoryRepository.existsByName(category.getName())) {
            throw new ValidationException("This name exist");
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    public CategoryDto getCategoryById(Long id) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found")));
    }

    public void deleteCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        Collection<Event> event = eventRepository.findAllByCategoryId(id);
        boolean check = event.isEmpty();
        if (!check) {
            throw new ValidationException("Category can't be delete");
        }
        categoryRepository.delete(category);
    }

    public CategoryDto updateCategoryById(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        if (categoryRepository.existsByName(categoryDto.getName()) && !(category.getName().equals(categoryDto.getName()) && !categoryDto.getName().isEmpty())) {
            throw new UpdateException("This name exist");
        }
        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    public List<CategoryDto> getCategories(int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Category> list = categoryRepository.findAll(page).getContent();

        return list.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }
}
