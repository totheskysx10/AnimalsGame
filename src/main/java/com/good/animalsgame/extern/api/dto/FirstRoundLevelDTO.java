package com.good.animalsgame.extern.api.dto;

import com.good.animalsgame.domain.AnimalCoordinates;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class FirstRoundLevelDTO {

    private Long id;

    @NotNull
    private byte[] imageWithAnimal;

    @NotNull
    private List<Long> animalIds;

    @NotNull
    private Long correctAnimalId;

    @NotNull
    private byte[] levelImage;

    @NotNull
    private AnimalCoordinates coordinates;
}
