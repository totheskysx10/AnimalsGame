package com.good.animalsgame.extern.api.assembler;

import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.SecondRoundLevel;
import com.good.animalsgame.extern.api.controller.FirstRoundLevelController;
import com.good.animalsgame.extern.api.controller.SecondRoundLevelController;
import com.good.animalsgame.extern.api.dto.SecondRoundLevelDTO;
import lombok.NonNull;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SecondRoundLevelAssembler extends RepresentationModelAssemblerSupport<SecondRoundLevel, SecondRoundLevelDTO> {

    public SecondRoundLevelAssembler() {
        super(SecondRoundLevelController.class, SecondRoundLevelDTO.class);
    }

    @Override
    public @NonNull SecondRoundLevelDTO toModel(@NonNull SecondRoundLevel secondRoundLevel) {
        SecondRoundLevelDTO secondRoundLevelDTO = instantiateModel(secondRoundLevel);

        secondRoundLevelDTO.setId(secondRoundLevel.getId());
        secondRoundLevelDTO.setImageWithAnimal(secondRoundLevel.getImageWithAnimal());
        if (!secondRoundLevel.getAnimals().isEmpty()) {
            secondRoundLevelDTO.setAnimalNames(secondRoundLevel.getAnimals()
                .stream()
                .map(Animal::getName)
                .collect(Collectors.toList()));
        }
        secondRoundLevelDTO.setCorrectAnimalName(secondRoundLevel.getCorrectAnimal().getName());
        secondRoundLevelDTO.setAnimalNameInQuestion(secondRoundLevel.getAnimalInQuestion().getName());

        secondRoundLevelDTO.add(linkTo(methodOn(SecondRoundLevelController.class).getLevelById(secondRoundLevel.getId())).withSelfRel());
        secondRoundLevelDTO.add(linkTo(methodOn(SecondRoundLevelController.class).getRandomLevel()).withSelfRel());

        return secondRoundLevelDTO;
    }
}
