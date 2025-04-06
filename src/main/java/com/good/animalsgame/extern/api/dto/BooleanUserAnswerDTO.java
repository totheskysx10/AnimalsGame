package com.good.animalsgame.extern.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "DTO ответа пользователя на вопрос да/нет")
public class BooleanUserAnswerDTO {

    @Schema(description = "Ответ пользователя")
    private Boolean answer;
}
