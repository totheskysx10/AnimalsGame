package com.good.animalsgame.extern.api.dto.level;

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
    @Schema(description = "Список возможных названий животных", example = "['Лев', 'Тигр', 'Кабан', 'Лиса']")
    private Set<String> animalNames;

    @NotNull
    @Schema(description = "Название животного - верный ответ", example = "Лев")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String correctAnimalName;

    @NotNull
    @Schema(description = "Название животного, о котором задан вопрос", example = "Тигр")
    private String animalNameInQuestion;

    @Schema(description = "Изображение уровня")
    @JsonSerialize(using = MultipartFileSerializer.class)
    private MultipartFile levelImage;

    @NotNull
    @Schema(description = "Координаты и размер области животного на изображении")
    private Coordinates animalCoordinates;
}
