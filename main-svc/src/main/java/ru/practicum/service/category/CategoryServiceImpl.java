package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

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
