package com.good.animalsgame.exception;

/**
 * Исключение дубликата сущности
 */
public class EntityDuplicateException extends Exception {
    public EntityDuplicateException(String message) {
        super(message);
    }
}
