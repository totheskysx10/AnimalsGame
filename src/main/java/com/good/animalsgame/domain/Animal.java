package com.good.animalsgame.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Сущность животного
 */
@Entity
@Table(name = "animals")
@AllArgsConstructor
@NoArgsConstructor
public class Animal {

    /**
     * Название животного
     */
    @Getter
    @Column(name = "animal_name")
    @Id
    private String name;

    /**
     * Описание животного
     */
    @Getter
    @Column(name = "animal_description", columnDefinition = "TEXT")
    private String description;

    /**
     * Уровни с животным в списке
     */
    @ManyToMany(mappedBy = "animals", fetch = FetchType.EAGER)
    private List<Level> associatedLevels;

    /**
     * Уровни, где это животное - правильный вариант
     */
    @OneToMany(mappedBy = "correctAnimal", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private List<Level> correctAnswerLevels;

    /**
     * Уровни второго раунда, где это животное указано в вопросе
     */
    @OneToMany(mappedBy = "animalInQuestion", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private List<SecondRoundLevel> secondRoundLevels;
}
