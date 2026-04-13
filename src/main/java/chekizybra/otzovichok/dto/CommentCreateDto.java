package chekizybra.otzovichok.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDto {
    private Long categoryId;
    private String title;
    private String comment;
    private Integer grade;
}
