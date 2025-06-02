package com.good.animalsgame.extern.api.controller;

import com.good.animalsgame.extern.api.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Обработчик исключений
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * Обрабатывает java.lang.Exception
     * @param e исключение
     */
    @ExceptionHandler(java.lang.Exception.class)
    public ResponseEntity<ErrorDTO> handleException(Exception e) {
        ErrorDTO errorResponse = new ErrorDTO(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
