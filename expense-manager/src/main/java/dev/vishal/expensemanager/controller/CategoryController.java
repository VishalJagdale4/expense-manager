package dev.vishal.expensemanager.controller;

import dev.vishal.expensemanager.common.exception.BadRequestException;
import dev.vishal.expensemanager.common.utils.ResponseDTO;
import dev.vishal.expensemanager.common.utils.ResponseUtil;
import dev.vishal.expensemanager.dto.CategoryDto;
import dev.vishal.expensemanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

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

        if (Objects.isNull(dto.getName())) {
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

        if (Objects.isNull(dto.getName())) {
            throw new BadRequestException("Name is mandatory!");
        }

        if (Objects.nonNull(dto.getParentCategoryId()) && Objects.equals(dto.getId(), dto.getParentCategoryId())) {
            throw new BadRequestException("Id cannot be same as Parent id!");
        }

        return ResponseUtil.sendResponse(categoryService.updateCategory(dto), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getCategory/{id}")
    public ResponseEntity<ResponseDTO> getCategory(@PathVariable Long id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getCategory";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        return ResponseUtil.sendResponse(categoryService.getCategory(id), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getCategoryByParent/{id}")
    public ResponseEntity<ResponseDTO> getCategoryByParent(@PathVariable Long id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getCategoryByParent";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        return ResponseUtil.sendResponse(categoryService.getCategoryByParent(id), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getAllCategories")
    public ResponseEntity<ResponseDTO> getAllCategories() throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getAllCategories";

        return ResponseUtil.sendResponse(categoryService.getAllCategories(), landingTime, HttpStatus.OK, endPoint);
    }

    @DeleteMapping("/deleteCategory/{id}")
    public ResponseEntity<ResponseDTO> deleteCategory(@PathVariable Long id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/deleteCategory";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        categoryService.deleteCategory(id);
        return ResponseUtil.sendResponse(id, landingTime, HttpStatus.OK, endPoint);
    }
}
