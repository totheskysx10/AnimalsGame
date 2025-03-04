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
     * Идентификатор животного
     */
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "animal_id")
    private Long id;

    /**
     * Название животного
     */
    @Getter
    @Column(name = "animal_name")
    @NonNull
    private String name;

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
