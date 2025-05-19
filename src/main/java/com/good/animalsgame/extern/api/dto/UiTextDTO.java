package com.good.animalsgame.extern.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для UI-текстов")
public class UiTextDTO extends RepresentationModel<UiTextDTO> {

    @Schema(description = "Идентификатор UI-текста", example = "1")
    private Long id;

    @Schema(description = "Название UI-текста", example = "Welcome caption")
    @NotNull
    @NotBlank
    private String title;

    @Schema(description = "Тексты на языках")
    private Map<String, String> texts;
}
