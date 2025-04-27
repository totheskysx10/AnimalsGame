package com.good.animalsgame.extern.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.good.animalsgame.domain.Coordinates;
import com.good.animalsgame.extern.api.assembler.MultipartFileSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@Schema(description = "DTO для уровня второго раунда")
public class SecondRoundLevelDTO extends RepresentationModel<SecondRoundLevelDTO> {

    @Schema(description = "Идентификатор уровня")
    private Long id;

    @NotNull
    @Schema(description = "Список возможных названий животных", example = "['1', '11', '12', '13']")
    private Set<Long> animalIds;

    @NotNull
    @Schema(description = "Id животного - верный ответ", example = "11")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long correctAnimalId;

    @NotNull
    @Schema(description = "Id животного, о котором задан вопрос", example = "12")
    private Long animalIdInQuestion;

    @Schema(description = "Изображение уровня")
    @JsonSerialize(using = MultipartFileSerializer.class)
    private MultipartFile levelImage;

    @NotNull
    @Schema(description = "Координаты и размер области животного на изображении")
    private Coordinates animalCoordinates;
}
