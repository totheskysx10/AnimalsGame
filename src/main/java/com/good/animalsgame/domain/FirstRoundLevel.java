package com.good.animalsgame.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Уровень первого раунда
 */
@Entity
@Table(name = "levels_first_round")
@NoArgsConstructor
@SuperBuilder
public class FirstRoundLevel extends Level {

}
