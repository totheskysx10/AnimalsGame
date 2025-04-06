package com.good.animalsgame.extern.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "DTO для животного")
public class AnimalDTO {

    @NotNull
    @Schema(description = "Название животного", example = "Лев")
    private String name;
}
