package ru.practicum.service.category;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getCategories(Integer from, Integer size);

    void deleteCategoryById(Long categoryId);

    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);

    CategoryDto getCategoryById(Long id);

    CategoryDto addNewCategory(NewCategoryDto newCategoryDto);

}
