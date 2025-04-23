package com.good.animalsgame.exception;

/**
 * Исключение, когда язык уже добавлен
 */
public class LanguageException extends Exception {
    public LanguageException(String message) {
        super(message);
    }
}
