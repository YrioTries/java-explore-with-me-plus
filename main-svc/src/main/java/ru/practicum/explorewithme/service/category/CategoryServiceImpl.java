package ru.practicum.explorewithme.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryDto;
import ru.practicum.explorewithme.exceptions.DuplicateCategoryException;
import ru.practicum.explorewithme.exceptions.NotFoundException;
import ru.practicum.explorewithme.mapper.CategoryMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.repository.CategoryRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto addNewCategory(NewCategoryDto newCategory) {
        log.info("В сервис пришёл запрос на создание категории");
        if (categoryRepository.findByName(newCategory.getName()).isPresent()) {
            log.error("Категория с таким названием уже существует");
            throw new DuplicateCategoryException("Категория с названием " + newCategory.getName() + " уже существует");
        }
        Category category = categoryRepository.save(categoryMapper.toCategory(newCategory));
        log.info("Новая категория успешно добавлена в БД. Её id={}", category.getId());
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long categoryId) {
        log.info("В сервис пришёл запрос на удаление категории");
        if (!categoryRepository.existsById(categoryId)) {
            log.error("Категории с id {} нет в БД", categoryId);
            throw new NotFoundException("Категории с id " + categoryId + " нет в БД. Удаление невозможно");
        }
        categoryRepository.deleteById(categoryId);
        log.info("Категория была успешно удалена из БД");
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto dto) {
        log.info("Сервис получил запрос на редактирование категории");
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категории с id " + catId + " нет в БД"));
        if (categoryRepository.findByName(dto.getName()).isPresent()) {
                if (!dto.getName().equals(category.getName())) {
                    log.error("Категория с таким названием уже есть");
                    throw new DuplicateCategoryException("Категория с названием " + dto.getName() + " уже существует");
                }
        }
        Optional.ofNullable(dto.getName()).ifPresent(category::setName);
        Category updatedCategory = categoryRepository.save(category);
        log.info("Категория с id {} успешно отредактирована", catId);
        return categoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
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
    public CategoryDto getCategoryById(Long id) {
        log.info("Сервис получил запрос на получение категории");
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категории с id " + id + " нет в БД"));
        log.info("Категория получена из БД и передаётся в контроллер");
        return categoryMapper.toCategoryDto(category);
    }
}
