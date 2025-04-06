package com.good.animalsgame.exception;

/**
 * Исключение, когда не найден раунд
 */
public class NoSuchRoundException extends Exception {
    public NoSuchRoundException(String message) {
        super(message);
    }
}
