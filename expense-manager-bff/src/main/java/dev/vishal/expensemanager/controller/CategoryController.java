package dev.vishal.expensemanager.controller;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.common.helper.SecurityUtils;
import dev.common.responseutils.ResponseUtil;
import dev.common.responseutils.model.ResponseDTO;
import dev.vishal.expensemanager.client.ExpenseManagerCoreClient;
import dev.vishal.expensemanager.dto.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final ExpenseManagerCoreClient expenseManagerCoreClient;

    @PostMapping("/createCategory")
    public ResponseEntity<ResponseDTO> createCategory(@RequestBody CategoryDto dto) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/createCategory";

        if (Objects.isNull(dto.getName())) {
            throw new BadRequestException("Name is mandatory!");
        }

        // Fetching user id from current user (Security context)
        dto.setUserId(SecurityUtils.getCurrentUser().getUserId());

        Object data = ResponseUtil.getDataFromResponse(expenseManagerCoreClient.createCategory(dto));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
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

        // Fetching user id from current user (Security context)
        dto.setUserId(SecurityUtils.getCurrentUser().getUserId());

        Object data = ResponseUtil.getDataFromResponse(expenseManagerCoreClient.updateCategory(dto));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getCategory/{id}")
    public ResponseEntity<ResponseDTO> getCategory(@PathVariable Long id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getCategory";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        // Fetching user id from current user (Security context)
        UUID userId = SecurityUtils.getCurrentUser().getUserId();

        Object data = ResponseUtil.getDataFromResponse(expenseManagerCoreClient.getCategory(userId, id));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getCategoryByParent/{id}")
    public ResponseEntity<ResponseDTO> getCategoryByParent(@PathVariable Long id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getCategoryByParent";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        // Fetching user id from current user (Security context)
        UUID userId = SecurityUtils.getCurrentUser().getUserId();

        Object data = ResponseUtil.getDataFromResponse(expenseManagerCoreClient.getCategoryByParent(userId, id));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getAllCategories")
    public ResponseEntity<ResponseDTO> getAllCategories() throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getAllCategories";

        // Fetching user id from current user (Security context)
        UUID userId = SecurityUtils.getCurrentUser().getUserId();

        Object data = ResponseUtil.getDataFromResponse(expenseManagerCoreClient.getAllCategories(userId));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
    }

    @DeleteMapping("/deleteCategory/{id}")
    public ResponseEntity<ResponseDTO> deleteCategory(@PathVariable Long id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/deleteCategory";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        // Fetching user id from current user (Security context)
        UUID userId = SecurityUtils.getCurrentUser().getUserId();

        expenseManagerCoreClient.deleteCategory(userId, id);
        return ResponseUtil.sendResponse(id, landingTime, HttpStatus.OK, endPoint);
    }
}
