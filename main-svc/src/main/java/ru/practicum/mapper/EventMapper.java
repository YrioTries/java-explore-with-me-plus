package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.UserRepository;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public abstract class EventMapper {

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected CategoryMapper categoryMapper;

    @Mapping(target = "category", expression = "java(mapCategoryIdToCategory(dto.getCategory()))")
    @Mapping(target = "initiator", expression = "java(mapInitiatorIdToUser(dto.getInitiatorId()))")
    public abstract Event toEvent(NewEventDto dto);

    @Mapping(target = "category", expression = "java(mapCategoryToCategoryDto(event.getCategory()))")
    @Mapping(target = "initiator.id", source = "initiator.id")
    @Mapping(target = "initiator.name", source = "initiator.name")
    public abstract EventFullDto toEventFullDto(Event event);

    @Mapping(target = "category", expression = "java(mapCategoryToCategoryDto(event.getCategory()))")
    @Mapping(target = "initiator.id", source = "initiator.id")
    @Mapping(target = "initiator.name", source = "initiator.name")
    public abstract EventShortDto toEventShortDto(Event event);

    protected Category mapCategoryIdToCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ru.practicum.exceptions.NotFoundException("Category not found with id: " + categoryId));
    }

    protected User mapInitiatorIdToUser(Long initiatorId) {
        if (initiatorId == null) {
            return null;
        }
        return userRepository.findById(initiatorId)
                .orElseThrow(() -> new ru.practicum.exceptions.NotFoundException("User not found with id: " + initiatorId));
    }

    protected ru.practicum.dto.category.CategoryDto mapCategoryToCategoryDto(Category category) {
        if (category == null) {
            return null;
        }
        return categoryMapper.toCategoryDto(category);
    }
}