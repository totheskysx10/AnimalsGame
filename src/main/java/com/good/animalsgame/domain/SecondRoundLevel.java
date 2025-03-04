package com.good.animalsgame.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Уровень второго раунда
 */
@Entity
@Table(name = "levels_second_round")
@AllArgsConstructor
@NoArgsConstructor
public class SecondRoundLevel extends Level {

    /**
     * Животное в вопросе
     */
    @ManyToOne
    @JoinColumn(name = "animal_in_question")
    @Getter
    private Animal animalInQuestion;
}
