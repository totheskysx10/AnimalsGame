package com.good.animalsgame.extern.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "DTO ответа пользователя на вопрос с выбором животного")
public class LongUserAnswerDTO {

    @Schema(description = "Ответ пользователя")
    Long answer;
}
