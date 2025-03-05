package com.good.animalsgame.extern.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnimalDTO {

    @NotNull
    private String name;
}
