package chekizybra.otzovichok.dto;

import chekizybra.otzovichok.model.Category;

public class CategoryDto {
    private Long id;
    private String category;

    public CategoryDto() {
    }

    public CategoryDto(Long id, String category) {
        this.id = id;
        this.category = category;
    }

    public static CategoryDto from(Category c) {
        return new CategoryDto(c.getId(), c.getCategory());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}