package chekizybra.otzovichok.controller;

import chekizybra.otzovichok.dto.CategoryDto;
import chekizybra.otzovichok.repository.CategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/roots")
    public List<CategoryDto> getRoots() {
        return categoryRepository.findByParentIsNull()
                .stream()
                .map(CategoryDto::from)
                .toList();
    }

    @GetMapping("/{id}/children")
    public List<CategoryDto> getChildren(@PathVariable Long id) {
        return categoryRepository.findByParentId(id)
                .stream()
                .map(CategoryDto::from)
                .toList();
    }
}