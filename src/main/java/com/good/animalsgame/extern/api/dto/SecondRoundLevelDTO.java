package com.good.animalsgame.extern.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
public class SecondRoundLevelDTO extends RepresentationModel<SecondRoundLevelDTO> {

    private Long id;

    @NotNull
    private byte[] imageWithAnimal;

    @NotNull
    private List<String> animalNames;

    @NotNull
    private String correctAnimalName;

    @NotNull
    private String animalNameInQuestion;
}
