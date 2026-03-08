package dev.vishal.expensemanager.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CategoryDto {
    private Long id;
    private UUID UserId;
    private String name;
    private Long parentCategoryId;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private Boolean deleted;
}
