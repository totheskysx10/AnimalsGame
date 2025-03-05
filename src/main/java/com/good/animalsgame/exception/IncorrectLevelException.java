package com.good.animalsgame.exception;

/**
 * Исключение, когда неверные данные для создания уровня
 */
public class IncorrectLevelException extends Exception {
    public IncorrectLevelException(String message) {
        super(message);
    }
}
