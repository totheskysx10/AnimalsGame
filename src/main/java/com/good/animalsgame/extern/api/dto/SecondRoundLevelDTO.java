package com.good.animalsgame.extern.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@Schema(description = "DTO для уровня второго раунда")
public class SecondRoundLevelDTO extends RepresentationModel<SecondRoundLevelDTO> {

    @Schema(description = "Идентификатор уровня")
    private Long id;

    @NotNull
    @Schema(description = "Изображение с выделенным животным в формате массива байтов")
    private byte[] imageWithAnimal;

    @NotNull
    @Schema(description = "Список возможных названий животных", example = "['Лев', 'Тигр', 'Пантера', 'Коза']")
    private List<String> animalNames;

    @NotNull
    @Schema(description = "Название животного - верный ответ", example = "Лев")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String correctAnimalName;

    @NotNull
    @Schema(description = "Название животного, о котором задан вопрос", example = "Тигр")
    private String animalNameInQuestion;
}
