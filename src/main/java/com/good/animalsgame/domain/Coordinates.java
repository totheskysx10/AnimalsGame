package com.good.animalsgame.domain;

import jakarta.persistence.Embeddable;

/**
 * Координаты области на картинке
 * @param x координата X левого верхнего угла области
 * @param y координата Y левого верхнего угла области
 * @param width ширина
 * @param height высота
 */
@Embeddable
public record Coordinates(int x, int y, int width, int height) {
}
