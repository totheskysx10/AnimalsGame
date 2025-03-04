package com.good.animalsgame.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Сущность уровня
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@AllArgsConstructor
@NoArgsConstructor
public class Level {

    /**
     * Идентификатор уровня
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Column(name = "level_id")
    private Long id;

    /**
     * Картинка с выделенным животным
     */
    @Lob
    @Column(name = "image_with_animal")
    @Getter
    @Setter
    private byte[] imageWithAnimal;

    /**
     * Список животных на уровне
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "level_animals",
            joinColumns = @JoinColumn(name = "level_id"),
            inverseJoinColumns = @JoinColumn(name = "animal_id")
    )
    @Getter
    private List<Animal> animals;

    /**
     * Корректное животное
     */
    @ManyToOne
    @JoinColumn(name = "correct_animal")
    @Getter
    private Animal correctAnimal;
}
