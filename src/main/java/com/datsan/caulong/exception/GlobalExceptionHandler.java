package com.datsan.caulong.exception;

import com.datsan.caulong.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidException(MethodArgumentNotValidException ex){
        List<String> errorMessage = new ArrayList<>();

        for(ObjectError error: ex.getBindingResult().getAllErrors()){
            errorMessage.add(error.getDefaultMessage());
        }

        ex.getBindingResult().getErrorCount();
        ApiResponse<List<String>> response = ApiResponse.<List<String>>builder()
                .data(errorMessage)
                .message("Dữ liệu không hợp lệ")
                .status("Error")
                .build();
        return ResponseEntity.ok(response);
    }
    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> handleAppException(AppException ex){
        ApiResponse<?> response = ApiResponse.builder()
                .message("Có lỗi xảy ra")
                .status("error")
                .data(ex.getError().getMessage())
                .build();

        return ResponseEntity.ok(response);
    }
}
