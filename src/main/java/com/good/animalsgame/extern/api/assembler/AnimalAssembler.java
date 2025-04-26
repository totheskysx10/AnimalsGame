package com.good.animalsgame.extern.api.assembler;

import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.Language;
import com.good.animalsgame.exception.LanguageException;
import com.good.animalsgame.extern.api.controller.AnimalController;
import com.good.animalsgame.extern.api.dto.AnimalDTO;
import lombok.NonNull;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AnimalAssembler extends RepresentationModelAssemblerSupport<Animal, AnimalDTO> {

    public AnimalAssembler() {
        super(AnimalController.class, AnimalDTO.class);
    }

    @Override
    public @NonNull AnimalDTO toModel(@NonNull Animal animal) {
        AnimalDTO animalDTO = instantiateModel(animal);

        animalDTO.setId(animal.getId());
        animalDTO.setNames(animal.getNames().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue)));
        animalDTO.setDescriptions(animal.getDescriptions().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue)));

        return animalDTO;
    }

    public Animal toEntity(AnimalDTO animalDTO) throws LanguageException {
        try {
            return Animal.builder()
                    .names(animalDTO.getNames().entrySet().stream()
                            .collect(Collectors.toMap(e -> Language.valueOf(e.getKey()), Map.Entry::getValue)))
                    .descriptions(animalDTO.getDescriptions().entrySet().stream()
                            .collect(Collectors.toMap(e -> Language.valueOf(e.getKey()), Map.Entry::getValue)))
                    .build();
        } catch (IllegalArgumentException e) {
            throw new LanguageException("Не найден язык! " + e.getMessage());
        }
    }
}
