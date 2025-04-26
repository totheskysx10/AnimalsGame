package com.good.animalsgame.extern.api.assembler;

import com.good.animalsgame.domain.Language;
import com.good.animalsgame.domain.UiText;
import com.good.animalsgame.exception.LanguageException;
import com.good.animalsgame.extern.api.controller.UiTextController;
import com.good.animalsgame.extern.api.dto.UiTextDTO;
import lombok.NonNull;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UiTextAssembler extends RepresentationModelAssemblerSupport<UiText, UiTextDTO> {
    public UiTextAssembler() {
        super(UiTextController.class, UiTextDTO.class);
    }

    @Override
    public @NonNull UiTextDTO toModel(@NonNull UiText uiText) {
        UiTextDTO uiTextDTO = instantiateModel(uiText);

        uiTextDTO.setId(uiText.getId());
        uiTextDTO.setTitle(uiText.getTitle());
        uiTextDTO.setTexts(uiText.getTexts().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue)));

        return uiTextDTO;
    }

    public UiText toEntity(UiTextDTO uiTextDTO) throws LanguageException {
        try {
            return UiText.builder()
                    .id(uiTextDTO.getId())
                    .title(uiTextDTO.getTitle())
                    .texts(uiTextDTO.getTexts().entrySet().stream()
                            .collect(Collectors.toMap(e -> Language.valueOf(e.getKey()), Map.Entry::getValue )))
                    .build();
        } catch (IllegalArgumentException e) {
            throw new LanguageException("Не найден язык! " + e.getMessage());
        }
    }
}
