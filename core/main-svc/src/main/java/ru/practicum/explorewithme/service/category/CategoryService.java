package ru.practicum.explorewithme.service.category;

import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(Pageable pageable);

    CategoryDto getCategoryById(Long catId);
}