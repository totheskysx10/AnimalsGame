package com.good.animalsgame.extern.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для добавления языка UI-текста")
public class AddUiTextLanguageDTO {

    @NotBlank
    @Schema(description = "Текст на языке", example = "Welcome!")
    private String text;
}
