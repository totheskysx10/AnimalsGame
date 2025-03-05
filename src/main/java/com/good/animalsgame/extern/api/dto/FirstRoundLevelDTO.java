package com.good.animalsgame.extern.api.dto;

import com.good.animalsgame.domain.Coordinates;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class FirstRoundLevelDTO {

    private Long id;

    @NotNull
    private byte[] imageWithAnimal;

    @NotNull
    private List<Long> animalNames;

    @NotNull
    private Long correctAnimalName;

    @NotNull
    private byte[] levelImage;

    @NotNull
    private Coordinates coordinates;
}
