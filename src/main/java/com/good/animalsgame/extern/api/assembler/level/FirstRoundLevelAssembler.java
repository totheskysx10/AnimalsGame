package com.good.animalsgame.extern.api.assembler.level;

import com.good.animalsgame.app.service.AnimalService;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.FirstRoundLevel;
import com.good.animalsgame.domain.Language;
import com.good.animalsgame.exception.EntityNotFoundException;
import com.good.animalsgame.exception.LanguageException;
import com.good.animalsgame.extern.api.assembler.CustomMultipartFile;
import com.good.animalsgame.extern.api.controller.FirstRoundLevelController;
import com.good.animalsgame.extern.api.dto.level.FirstRoundLevelDTO;
import lombok.NonNull;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
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

    /**
     * Маппит уровень 1 раунда в DTO на стандартном русском языке
     * @param firstRoundLevel уровень 1 раунда
     */
    @Override
    public @NonNull FirstRoundLevelDTO toModel(@NonNull FirstRoundLevel firstRoundLevel) {
        FirstRoundLevelDTO firstRoundLevelDTO = instantiateModel(firstRoundLevel);

        firstRoundLevelDTO.setId(firstRoundLevel.getId());
        firstRoundLevelDTO.setLevelImage(new CustomMultipartFile(firstRoundLevel.getLevelImage(), "image", "image/jpeg"));
        firstRoundLevelDTO.setAnimalNames(firstRoundLevel.getAnimals()
                .stream()
                .map(animal -> animal.getNames().get(Language.RUSSIAN))
                .collect(Collectors.toSet()));
        firstRoundLevelDTO.setCorrectAnimalName(firstRoundLevel.getCorrectAnimal().getNames().get(Language.RUSSIAN));
        firstRoundLevelDTO.setAnimalCoordinates(firstRoundLevel.getAnimalCoordinates());

        firstRoundLevelDTO.add(linkTo(methodOn(FirstRoundLevelController.class).getLevelById(firstRoundLevel.getId(), "RUSSIAN")).withSelfRel());
        firstRoundLevelDTO.add(linkTo(methodOn(FirstRoundLevelController.class).getRandomLevel("RUSSIAN")).withSelfRel());

        return firstRoundLevelDTO;
    }

    /**
     * Маппит уровень 1 раунда в DTO на выбранном языке
     * @param firstRoundLevel уровень 1 раунда
     * @param language язык
     */
    public @NonNull FirstRoundLevelDTO toModel(@NonNull FirstRoundLevel firstRoundLevel, String language) throws LanguageException, EntityNotFoundException {
        FirstRoundLevelDTO firstRoundLevelDTO = instantiateModel(firstRoundLevel);

        Set<String> animalNames = new HashSet<>();
        for (Animal animal : firstRoundLevel.getAnimals()) {
            animalNames.add(animalService.getAnimalSingleLanguageData(animal.getId(), language).name());
        }

        firstRoundLevelDTO.setId(firstRoundLevel.getId());
        firstRoundLevelDTO.setLevelImage(new CustomMultipartFile(firstRoundLevel.getLevelImage(), "image", "image/jpeg"));
        firstRoundLevelDTO.setAnimalNames(animalNames);
        firstRoundLevelDTO.setCorrectAnimalName(animalService.getAnimalSingleLanguageData(firstRoundLevel.getCorrectAnimal().getId(), language).name());
        firstRoundLevelDTO.setAnimalCoordinates(firstRoundLevel.getAnimalCoordinates());

        firstRoundLevelDTO.add(linkTo(methodOn(FirstRoundLevelController.class).getLevelById(firstRoundLevel.getId(), language)).withSelfRel());
        firstRoundLevelDTO.add(linkTo(methodOn(FirstRoundLevelController.class).getRandomLevel(language)).withSelfRel());

        return firstRoundLevelDTO;
    }

    public FirstRoundLevel toEntity(FirstRoundLevelDTO firstRoundLevelDTO) throws EntityNotFoundException, IOException {
        Set<Animal> animals = new HashSet<>();
        for (String animalName : firstRoundLevelDTO.getAnimalNames()) {
            Animal animal = animalService.getAnimalByName(animalName);
            animals.add(animal);
        }

        return FirstRoundLevel.builder()
                .animals(animals)
                .correctAnimal(animalService.getAnimalByName(firstRoundLevelDTO.getCorrectAnimalName()))
                .levelImage(firstRoundLevelDTO.getLevelImage().getBytes())
                .animalCoordinates(firstRoundLevelDTO.getAnimalCoordinates())
                .build();
    }
}
