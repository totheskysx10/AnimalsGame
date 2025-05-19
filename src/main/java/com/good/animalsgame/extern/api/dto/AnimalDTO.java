package com.good.animalsgame.extern.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для животного")
public class AnimalDTO extends RepresentationModel<AnimalDTO> {

    @Schema(description = "ID животного", example = "1")
    private Long id;

    @NotNull
    @Schema(description = "Названия животного на языках", example = "RUSSIAN Лев")
    private Map<String, String> names;

    @Schema(description = "Описания животного на языках")
    @NotNull
    private Map<String, String> descriptions;
}
