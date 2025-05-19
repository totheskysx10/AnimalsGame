package com.good.animalsgame.exception;

/**
 * Исключение, когда сущность не найдена
 */
public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
