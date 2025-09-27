package ru.practicum.explorewithme.service.category;

import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    void deleteCategoryById(Long categoryId);

    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);

    CategoryDto getCategoryById(Long id);

    CategoryDto addNewCategory(NewCategoryDto newCategoryDto);

}
