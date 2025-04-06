package com.good.animalsgame.extern.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "DTO ошибки")
public class ErrorDTO {

    @Schema(description = "Текст ошибки")
    private String error;
}
