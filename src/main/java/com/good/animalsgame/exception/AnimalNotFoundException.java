package com.good.animalsgame.exception;

/**
 * Исключение, когда животное не найдено
 */
public class AnimalNotFoundException extends Exception {
    public AnimalNotFoundException(String message) {
        super(message);
    }
}
