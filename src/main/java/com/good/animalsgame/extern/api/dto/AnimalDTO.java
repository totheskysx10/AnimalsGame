package com.good.animalsgame.extern.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnimalDTO {

    @NotNull
    private String name;
}
