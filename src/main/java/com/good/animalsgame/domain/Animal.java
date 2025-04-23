package com.good.animalsgame.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * Сущность животного
 */
@Entity
@Table(name = "animals")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Animal {

    /**
     * Идентификатор животного
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    /**
     * Название животного
     */
    @Getter
    @Column(name = "animal_name")
    @ElementCollection
    @CollectionTable(
            name = "animal_name",
            joinColumns = @JoinColumn(name = "animal_id")
    )
    @MapKeyColumn(name = "language")
    private Map<Language, String> name;

    /**
     * Описание животного
     */
    @Getter
    @Column(name = "animal_description", columnDefinition = "TEXT")
    @ElementCollection
    @CollectionTable(
            name = "animal_description",
            joinColumns = @JoinColumn(name = "animal_id")
    )
    @MapKeyColumn(name = "language")
    private Map<Language, String> description;

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
