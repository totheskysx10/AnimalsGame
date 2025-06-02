package com.good.animalsgame.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * Сущность уровня
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@SuperBuilder
public abstract class Level {

    /**
     * Идентификатор уровня
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Column(name = "level_id")
    private Long id;

    /**
     * Список животных на уровне
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "level_animals",
            joinColumns = @JoinColumn(name = "level_id"),
            inverseJoinColumns = @JoinColumn(name = "animal_name")
    )
    @Getter
    private Set<Animal> animals;

    /**
     * Корректное животное
     */
    @ManyToOne
    @JoinColumn(name = "correct_animal")
    @Getter
    private Animal correctAnimal;

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
