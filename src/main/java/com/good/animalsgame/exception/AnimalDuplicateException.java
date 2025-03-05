package com.good.animalsgame.exception;

/**
 * Исключение дубликата животного
 */
public class AnimalDuplicateException extends Exception {
    public AnimalDuplicateException(String message) {
        super(message);
    }
}
