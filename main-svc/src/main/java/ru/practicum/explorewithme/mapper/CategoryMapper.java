package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryDto;
import ru.practicum.explorewithme.model.Category;

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