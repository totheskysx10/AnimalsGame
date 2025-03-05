package com.good.animalsgame.extern.api.dto;

import com.good.animalsgame.domain.Coordinates;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
public class FirstRoundLevelDTO extends RepresentationModel<FirstRoundLevelDTO> {

    private Long id;

    @NotNull
    private byte[] imageWithAnimal;

    @NotNull
    @NotEmpty
    private List<String> animalNames;

    @NotNull
    private String correctAnimalName;

    @NotNull
    private byte[] levelImage;

    @NotNull
    private Coordinates animalCoordinates;
}
