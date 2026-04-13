package chekizybra.otzovichok.dto;
import chekizybra.otzovichok.model.Comment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentReadDto {
    private Long id;
    private String title;
    private String comment;
    private Integer grade;
    private String postDate;
    private Long plusGrade;
    private Long minusGrade;
    private CategoryDto category;
    public static CommentReadDto from(Comment c, long plus, long minus) {
        CommentReadDto dto = new CommentReadDto();
        dto.setId(c.getId());
        dto.setTitle(c.getTitle());
        dto.setComment(c.getComment());
        dto.setGrade(c.getGrade());
        dto.setPostDate(c.getPostDate() != null ? c.getPostDate().toString() : null);
        dto.setPlusGrade(plus);
        dto.setMinusGrade(minus);
        if (c.getCategory() != null) {
            dto.setCategory(new CategoryDto(c.getCategory().getId(), c.getCategory().getCategory()));
        }
        return dto;
    }
}

