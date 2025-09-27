package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);

    Category toCategory(NewCategoryDto dto);

    default CategoryDto map(Long id) {
        if (id == null) {
            return null;
        }

        CategoryDto dto = new CategoryDto();
        dto.setId(id);
        return dto;
    }
}