package com.good.animalsgame.exception;

/**
 * Исключение, когда не осталось уровней в раунде для данной сессии
 */
public class NoLevelsLeftException extends Exception {
    public NoLevelsLeftException(String message) {
        super(message);
    }
}
