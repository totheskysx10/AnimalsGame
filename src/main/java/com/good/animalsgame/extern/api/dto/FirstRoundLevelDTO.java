package com.good.animalsgame.extern.api.dto;

import com.good.animalsgame.domain.Coordinates;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@Schema(description = "DTO для уровня первого раунда")
public class FirstRoundLevelDTO extends RepresentationModel<FirstRoundLevelDTO> {

    @Schema(description = "Идентификатор уровня")
    private Long id;

    @NotNull
    @Schema(description = "Изображение с выделенным животным в формате массива байтов")
    private byte[] imageWithAnimal;

    @NotNull
    @NotEmpty
    @Schema(description = "Список возможных названий животных", example = "['Лев', 'Тигр', 'Пантера', 'Коза']")
    private List<String> animalNames;

    @NotNull
    @Schema(description = "Название животного - верный ответ", example = "Лев")
    private String correctAnimalName;

    @NotNull
    @Schema(description = "Изображение уровня в формате массива байтов")
    private byte[] levelImage;

    @NotNull
    @Schema(description = "Координаты и размер области животного на изображении")
    private Coordinates animalCoordinates;
}

