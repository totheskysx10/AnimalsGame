package com.good.animalsgame.extern.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.good.animalsgame.domain.Coordinates;
import com.good.animalsgame.extern.api.assembler.MultipartFileSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@Schema(description = "DTO для уровня первого раунда")
public class FirstRoundLevelDTO extends RepresentationModel<FirstRoundLevelDTO> {

    @Schema(description = "Идентификатор уровня")
    private Long id;

    @NotNull
    @NotEmpty
    @Schema(description = "Список возможных ID животных", example = "['1', '11', '12', '13']")
    private Set<Long> animalIds;

    @NotNull
    @Schema(description = "ID животного - верный ответ", example = "1")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long correctAnimalId;

    @Schema(description = "Изображение уровня")
    @JsonSerialize(using = MultipartFileSerializer.class)
    private MultipartFile levelImage;

    @NotNull
    @Schema(description = "Координаты и размер области животного на изображении")
    private Coordinates animalCoordinates;
}

