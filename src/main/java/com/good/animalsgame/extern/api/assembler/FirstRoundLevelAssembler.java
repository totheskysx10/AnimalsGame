package com.good.animalsgame.extern.api.assembler;

import com.good.animalsgame.app.service.AnimalService;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.FirstRoundLevel;
import com.good.animalsgame.exception.AnimalNotFoundException;
import com.good.animalsgame.extern.api.controller.FirstRoundLevelController;
import com.good.animalsgame.extern.api.dto.FirstRoundLevelDTO;
import lombok.NonNull;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

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
        firstRoundLevelDTO.setLevelImage(firstRoundLevel.getLevelImage());
        firstRoundLevelDTO.setAnimalNames(firstRoundLevel.getAnimals()
                .stream()
                .map(Animal::getName)
                .collect(Collectors.toList()));
        firstRoundLevelDTO.setCorrectAnimalName(firstRoundLevel.getCorrectAnimal().getName());
        firstRoundLevelDTO.setImageWithAnimal(firstRoundLevel.getImageWithAnimal());
        firstRoundLevelDTO.setAnimalCoordinates(firstRoundLevel.getAnimalCoordinates());

        firstRoundLevelDTO.add(linkTo(methodOn(FirstRoundLevelController.class).getLevelById(firstRoundLevel.getId())).withSelfRel());
        firstRoundLevelDTO.add(linkTo(methodOn(FirstRoundLevelController.class).getRandomLevel()).withSelfRel());

        return firstRoundLevelDTO;
    }

    public FirstRoundLevel toEntity(FirstRoundLevelDTO firstRoundLevelDTO) throws AnimalNotFoundException {
        List<Animal> animals = new ArrayList<>();
        for (String animalName : firstRoundLevelDTO.getAnimalNames()) {
            Animal animal = animalService.getAnimalByName(animalName);
            animals.add(animal);
        }

        return FirstRoundLevel.builder()
                .imageWithAnimal(firstRoundLevelDTO.getImageWithAnimal())
                .animals(animals)
                .correctAnimal(animalService.getAnimalByName(firstRoundLevelDTO.getCorrectAnimalName()))
                .levelImage(firstRoundLevelDTO.getLevelImage())
                .animalCoordinates(firstRoundLevelDTO.getAnimalCoordinates())
                .build();
    }
}
