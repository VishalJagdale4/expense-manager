package dev.vishal.expensemanager.controller;

import dev.vishal.expensemanager.common.exception.BadRequestException;
import dev.vishal.expensemanager.common.utils.ResponseDTO;
import dev.vishal.expensemanager.common.utils.ResponseUtil;
import dev.vishal.expensemanager.dto.TransactionDto;
import dev.vishal.expensemanager.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/createTransaction")
    public ResponseEntity<ResponseDTO> createTransaction(@RequestBody TransactionDto dto) {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/createTransaction";

        if (Objects.isNull(dto.getAmount())) {
            throw new BadRequestException("Amount is mandatory!");
        }

        if (dto.getAmount().equals(BigDecimal.ZERO)) {
            throw new BadRequestException("Amount value 0 is not allowed!");
        }

        if (!StringUtils.hasText(dto.getNote())) {
            throw new BadRequestException("Note is mandatory!");
        }

        if (Objects.isNull(dto.getTransactionType())) {
            throw new BadRequestException("Transaction type is mandatory!");
        }

        if (Objects.isNull(dto.getAccountId())) {
            throw new BadRequestException("Account id is mandatory!");
        }

        if (Objects.isNull(dto.getCategoryId())) {
            throw new BadRequestException("Category id is mandatory!");
        }

        if (Objects.isNull(dto.getTransactionDatetime())) {
            throw new BadRequestException("Transaction datetime is mandatory!");
        }

        return ResponseUtil.sendResponse(transactionService.createTransaction(dto), landingTime, HttpStatus.OK, endPoint);
    }

    @PutMapping("/updateTransaction")
    public ResponseEntity<ResponseDTO> updateTransaction(@RequestBody TransactionDto dto) {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/updateTransaction";

        if (Objects.isNull(dto.getId())) {
            throw new BadRequestException("Id is mandatory!");
        }

        if (Objects.isNull(dto.getAmount())) {
            throw new BadRequestException("Amount is mandatory!");
        }

        if (!StringUtils.hasText(dto.getNote())) {
            throw new BadRequestException("Note is mandatory!");
        }

        if (Objects.isNull(dto.getTransactionType())) {
            throw new BadRequestException("Transaction type is mandatory!");
        }

        if (Objects.isNull(dto.getAccountId())) {
            throw new BadRequestException("Account id is mandatory!");
        }

        if (Objects.isNull(dto.getCategoryId())) {
            throw new BadRequestException("Category id is mandatory!");
        }

        if (Objects.isNull(dto.getTransactionDatetime())) {
            throw new BadRequestException("Transaction datetime is mandatory!");
        }

        return ResponseUtil.sendResponse(transactionService.updateTransaction(dto), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getTransaction/{id}")
    public ResponseEntity<ResponseDTO> getTransaction(@PathVariable UUID id) {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getTransaction";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        return ResponseUtil.sendResponse(transactionService.getTransaction(id), landingTime, HttpStatus.OK, endPoint);
    }

    @PostMapping("/getAllTransactions")
    public ResponseEntity<ResponseDTO> getAllTransactions(@RequestBody TransactionDto dto) {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getAllTransactions";

        return ResponseUtil.sendResponse(transactionService.getAllTransactions(dto), landingTime, HttpStatus.OK, endPoint);
    }

    @PostMapping("/getTransactionNotes")
    public ResponseEntity<ResponseDTO> getTransactionNotes(@RequestBody TransactionDto dto) {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getTransactionNotes";

        List<String> notes = new ArrayList<>();

        if(StringUtils.hasText(dto.getNoteLike())){
            dto.setNoteLike(dto.getNoteLike().strip());
            notes = transactionService.getTransactionNotes(dto);
        }

        return ResponseUtil.sendResponse(notes, landingTime, HttpStatus.OK, endPoint);
    }

    @DeleteMapping("/deleteTransaction/{id}")
    public ResponseEntity<ResponseDTO> deleteTransaction(@PathVariable UUID id) {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/deleteTransaction";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        transactionService.deleteTransaction(id);
        return ResponseUtil.sendResponse(id, landingTime, HttpStatus.OK, endPoint);
    }
}
