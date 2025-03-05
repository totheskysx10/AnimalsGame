package com.good.animalsgame.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Уровень первого раунда
 */
@Entity
@Table(name = "levels_first_round")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
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
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "animal_x")),
            @AttributeOverride(name = "y", column = @Column(name = "animal_y")),
            @AttributeOverride(name = "width", column = @Column(name = "animal_width")),
            @AttributeOverride(name = "height", column = @Column(name = "animal_height"))
    })
    @Getter
    private Coordinates animalCoordinates;
}
