package com.good.animalsgame.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Уровень первого раунда
 */
@Entity
@Table(name = "levels_first_round")
@AllArgsConstructor
@NoArgsConstructor
public class FirstRoundLevel extends Level {

    /**
     * Картинка без выделенного животного
     */
    @Lob
    @Column(name = "level_image")
    @Getter
    @Setter
    private byte[] levelImage;

    /**
     * Координаты животного на картинке
     */
    @Embedded
    @Getter
    @Setter
    private AnimalCoordinates coordinates;
}
