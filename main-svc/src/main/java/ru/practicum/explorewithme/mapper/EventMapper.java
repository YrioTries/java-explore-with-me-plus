package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventDto;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.exceptions.NotFoundException;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.UserRepository;

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
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + categoryId));
    }

    protected User mapInitiatorIdToUser(Long initiatorId) {
        if (initiatorId == null) {
            return null;
        }
        return userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + initiatorId));
    }

    protected CategoryDto mapCategoryToCategoryDto(Category category) {
        if (category == null) {
            return null;
        }
        return categoryMapper.toCategoryDto(category);
    }
}