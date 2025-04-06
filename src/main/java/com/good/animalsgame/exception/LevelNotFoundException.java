package com.good.animalsgame.exception;

/**
 * Исключение, когда уровень не найден
 */
public class LevelNotFoundException extends Exception {
    public LevelNotFoundException(String message) {
        super(message);
    }
}
