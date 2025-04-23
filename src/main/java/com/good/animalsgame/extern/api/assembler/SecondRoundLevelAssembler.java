package com.good.animalsgame.extern.api.assembler;

import com.good.animalsgame.app.service.AnimalService;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.SecondRoundLevel;
import com.good.animalsgame.exception.AnimalNotFoundException;
import com.good.animalsgame.extern.api.controller.SecondRoundLevelController;
import com.good.animalsgame.extern.api.dto.SecondRoundLevelDTO;
import lombok.NonNull;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SecondRoundLevelAssembler extends RepresentationModelAssemblerSupport<SecondRoundLevel, SecondRoundLevelDTO> {

    private final AnimalService animalService;

    public SecondRoundLevelAssembler(AnimalService animalService) {
        super(SecondRoundLevelController.class, SecondRoundLevelDTO.class);
        this.animalService = animalService;
    }

    @Override
    public @NonNull SecondRoundLevelDTO toModel(@NonNull SecondRoundLevel secondRoundLevel) {
        SecondRoundLevelDTO secondRoundLevelDTO = instantiateModel(secondRoundLevel);

        secondRoundLevelDTO.setId(secondRoundLevel.getId());
        if (!secondRoundLevel.getAnimals().isEmpty()) {
            secondRoundLevelDTO.setAnimalIds(secondRoundLevel.getAnimals()
                .stream()
                .map(Animal::getId)
                .collect(Collectors.toList()));
        }
        secondRoundLevelDTO.setCorrectAnimalId(secondRoundLevel.getCorrectAnimal().getId());
        secondRoundLevelDTO.setAnimalIdInQuestion(secondRoundLevel.getAnimalInQuestion().getId());
        secondRoundLevelDTO.setLevelImage(new CustomMultipartFile(secondRoundLevel.getLevelImage(), "image", "image/png"));
        secondRoundLevelDTO.setAnimalCoordinates(secondRoundLevel.getAnimalCoordinates());

        secondRoundLevelDTO.add(linkTo(methodOn(SecondRoundLevelController.class).getLevelById(secondRoundLevel.getId())).withSelfRel());
        secondRoundLevelDTO.add(linkTo(methodOn(SecondRoundLevelController.class).getRandomLevel()).withSelfRel());

        return secondRoundLevelDTO;
    }

    public SecondRoundLevel toEntity(SecondRoundLevelDTO secondRoundLevelDTO) throws AnimalNotFoundException, IOException {
        List<Animal> animals = new ArrayList<>();
        for (Long animalId : secondRoundLevelDTO.getAnimalIds()) {
            Animal animal = animalService.getAnimalById(animalId);
            animals.add(animal);
        }

        return SecondRoundLevel.builder()
                .animals(animals)
                .correctAnimal(animalService.getAnimalById(secondRoundLevelDTO.getCorrectAnimalId()))
                .animalInQuestion(animalService.getAnimalById(secondRoundLevelDTO.getAnimalIdInQuestion()))
                .levelImage(secondRoundLevelDTO.getLevelImage().getBytes())
                .animalCoordinates(secondRoundLevelDTO.getAnimalCoordinates())
                .build();
    }
}
