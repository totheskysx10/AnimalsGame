package com.good.animalsgame.extern.api.assembler;

import com.good.animalsgame.app.service.AnimalService;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.FirstRoundLevel;
import com.good.animalsgame.exception.EntityNotFoundException;
import com.good.animalsgame.extern.api.controller.FirstRoundLevelController;
import com.good.animalsgame.extern.api.dto.FirstRoundLevelDTO;
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
public class FirstRoundLevelAssembler extends RepresentationModelAssemblerSupport<FirstRoundLevel, FirstRoundLevelDTO> {

    private final AnimalService animalService;

    public FirstRoundLevelAssembler(AnimalService animalService) {
        super(FirstRoundLevelController.class, FirstRoundLevelDTO.class);
        this.animalService = animalService;
    }

    @Override
    public @NonNull FirstRoundLevelDTO toModel(@NonNull FirstRoundLevel firstRoundLevel) {
        FirstRoundLevelDTO firstRoundLevelDTO = instantiateModel(firstRoundLevel);

        firstRoundLevelDTO.setId(firstRoundLevel.getId());
        firstRoundLevelDTO.setLevelImage(new CustomMultipartFile(firstRoundLevel.getLevelImage(), "image", "image/png"));
        firstRoundLevelDTO.setAnimalIds(firstRoundLevel.getAnimals()
                .stream()
                .map(Animal::getId)
                .collect(Collectors.toList()));
        firstRoundLevelDTO.setCorrectAnimalId(firstRoundLevel.getCorrectAnimal().getId());
        firstRoundLevelDTO.setAnimalCoordinates(firstRoundLevel.getAnimalCoordinates());

        firstRoundLevelDTO.add(linkTo(methodOn(FirstRoundLevelController.class).getLevelById(firstRoundLevel.getId())).withSelfRel());
        firstRoundLevelDTO.add(linkTo(methodOn(FirstRoundLevelController.class).getRandomLevel()).withSelfRel());

        return firstRoundLevelDTO;
    }

    public FirstRoundLevel toEntity(FirstRoundLevelDTO firstRoundLevelDTO) throws EntityNotFoundException, IOException {
        List<Animal> animals = new ArrayList<>();
        for (Long animalId : firstRoundLevelDTO.getAnimalIds()) {
            Animal animal = animalService.getAnimalById(animalId);
            animals.add(animal);
        }

        return FirstRoundLevel.builder()
                .animals(animals)
                .correctAnimal(animalService.getAnimalById(firstRoundLevelDTO.getCorrectAnimalId()))
                .levelImage(firstRoundLevelDTO.getLevelImage().getBytes())
                .animalCoordinates(firstRoundLevelDTO.getAnimalCoordinates())
                .build();
    }
}
