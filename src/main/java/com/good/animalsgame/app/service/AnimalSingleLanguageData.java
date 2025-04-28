package com.good.animalsgame.app.service;

/**
 * Информация об одном животном на одном языке.
 * Для добавления языка
 * @param name название
 * @param description описание
 */
public record AnimalSingleLanguageData(String name, String description) {
}
