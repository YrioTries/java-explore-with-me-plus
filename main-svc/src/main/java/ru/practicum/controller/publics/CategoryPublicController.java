package ru.practicum.controller.publics;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.category.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories(
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("В контроллер пришёл запрос на получение списка категорий");
        log.info("Параметры строки запроса: from={}, size={}", from, size);
        List<CategoryDto> categoryDtos = categoryService.getAllCategories(from, size);
        log.info("Возвращаем список категорий клиенту. Размер списка: {}", categoryDtos.size());
        return categoryDtos;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PositiveOrZero @PathVariable Long catId) {
        log.info("В контроллер пришёл запрос на получение категории");
        log.info("Id запрашиваемой категории: {}", catId);
        CategoryDto categoryDto = categoryService.getCategoryById(catId);
        log.info("Возвращаем категорию клиенту");
        return null;
    }
}
