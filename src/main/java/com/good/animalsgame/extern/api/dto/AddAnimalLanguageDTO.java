package com.good.animalsgame.extern.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для добавления языка животного")
public class AddAnimalLanguageDTO {

    @NotNull
    @Schema(description = "Название животного", example = "Лев")
    private String name;

    @Schema(description = "Описание животного")
    private String description;
}
