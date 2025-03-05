package com.good.animalsgame.extern.api.dto;

import com.good.animalsgame.domain.Animal;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SecondRoundLevelDTO {

    private Long id;

    @NotNull
    private byte[] imageWithAnimal;

    private List<Long> animalNames;

    @NotNull
    private Long correctAnimalName;

    @NotNull
    private Animal animalNameInQuestion;
}
