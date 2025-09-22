package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        log.info("Сервис получил запрос на получение списка категорий");
        PageRequest pageRequest = PageRequest.of(from, size);
        List<CategoryDto> categories = categoryRepository.findAll(pageRequest)
                .stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
        log.info("Список категорий вернулся из БД");
        return categories;
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        log.info("Сервис получил запрос на получение категории");
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категории с id " + id + " нет в БД"));
        log.info("Категория получена из БД и передаётся в контроллер");
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("Создание новой категории: {}", newCategoryDto);
        try {
            Category category = new Category();
            category.setName(newCategoryDto.getName());
            Category savedCategory = categoryRepository.save(category);
            return categoryMapper.toCategoryDto(savedCategory);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Категория с именем '" + newCategoryDto.getName() + "' уже существует");
        }
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        log.info("Удаление категории с id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категории с id " + id + " нет в БД"));

        // Проверка, что с категорией не связано ни одного события
        boolean hasEvents = eventRepository.existsByCategoryId(id);
        if (hasEvents) {
            throw new ConflictException("Нельзя удалить категорию с id " + id + ", так как с ней связаны события");
        }

        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        log.info("Обновление категории с id: {} данными: {}", id, categoryDto);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категории с id " + id + " нет в БД"));

        if (categoryDto.getName() != null && !categoryDto.getName().isBlank()) {
            // Проверка на уникальность имени
            categoryRepository.findByName(categoryDto.getName())
                    .ifPresent(existingCategory -> {
                        if (!existingCategory.getId().equals(id)) {
                            throw new ConflictException("Категория с именем '" + categoryDto.getName() + "' уже существует");
                        }
                    });
            category.setName(categoryDto.getName());
        }

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(updatedCategory);
    }
}