package dev.vishal.expensemanager.controller;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.common.responseutils.ResponseUtil;
import dev.common.responseutils.model.ResponseDTO;
import dev.vishal.expensemanager.dto.CategoryDto;
import dev.vishal.expensemanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/createCategory")
    public ResponseEntity<ResponseDTO> createCategory(@RequestBody CategoryDto dto) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/createCategory";

        if (Objects.isNull(dto.getUserId())) {
            throw new BadRequestException("User Id is mandatory!");
        }

        if (!StringUtils.hasText(dto.getName())) {
            throw new BadRequestException("Name is mandatory!");
        }

        return ResponseUtil.sendResponse(categoryService.createCategory(dto), landingTime, HttpStatus.OK, endPoint);
    }

    @PutMapping("/updateCategory")
    public ResponseEntity<ResponseDTO> updateCategory(@RequestBody CategoryDto dto) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/updateCategory";

        if (Objects.isNull(dto.getId())) {
            throw new BadRequestException("Id is mandatory!");
        }

        if (Objects.isNull(dto.getUserId())) {
            throw new BadRequestException("User Id is mandatory!");
        }

        if (!StringUtils.hasText(dto.getName())) {
            throw new BadRequestException("Name is mandatory!");
        }

        if (Objects.nonNull(dto.getParentCategoryId()) && Objects.equals(dto.getId(), dto.getParentCategoryId())) {
            throw new BadRequestException("Id cannot be same as Parent id!");
        }

        return ResponseUtil.sendResponse(categoryService.updateCategory(dto), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getCategory/{userId}/{id}")
    public ResponseEntity<ResponseDTO> getCategory(
            @PathVariable Long id,
            @PathVariable UUID userId) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getCategory";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        if (Objects.isNull(userId)) {
            throw new BadRequestException("User Id is mandatory!");
        }

        return ResponseUtil.sendResponse(
                categoryService.getCategory(id, userId), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getCategoryByParent/{userId}/{id}")
    public ResponseEntity<ResponseDTO> getCategoryByParent(
            @PathVariable Long id,
            @PathVariable UUID userId) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getCategoryByParent";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        if (Objects.isNull(userId)) {
            throw new BadRequestException("User Id is mandatory!");
        }

        return ResponseUtil.sendResponse(
                categoryService.getCategoryByParent(id, userId), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getAllCategories/{userId}")
    public ResponseEntity<ResponseDTO> getAllCategories(@PathVariable UUID userId) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getAllCategories";

        if (Objects.isNull(userId)) {
            throw new BadRequestException("User Id is mandatory!");
        }

        return ResponseUtil.sendResponse(
                categoryService.getAllCategories(userId), landingTime, HttpStatus.OK, endPoint);
    }

    @DeleteMapping("/deleteCategory/{userId}/{id}")
    public ResponseEntity<ResponseDTO> deleteCategory(
            @PathVariable Long id,
            @PathVariable UUID userId) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/deleteCategory";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        if (Objects.isNull(userId)) {
            throw new BadRequestException("User Id is mandatory!");
        }

        categoryService.deleteCategory(id, userId);
        return ResponseUtil.sendResponse(id, landingTime, HttpStatus.OK, endPoint);
    }
}
