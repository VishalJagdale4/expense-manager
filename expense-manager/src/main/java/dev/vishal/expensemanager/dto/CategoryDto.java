package dev.vishal.expensemanager.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryDto {
    private Long id;
    private String name;
    private Long parentCategoryId;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private Boolean deleted;
}
