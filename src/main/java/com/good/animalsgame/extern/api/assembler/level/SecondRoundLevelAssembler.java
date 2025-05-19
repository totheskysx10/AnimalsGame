package com.good.animalsgame.extern.api.assembler.level;

import com.good.animalsgame.app.service.AnimalService;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.Language;
import com.good.animalsgame.domain.SecondRoundLevel;
import com.good.animalsgame.exception.EntityNotFoundException;
import com.good.animalsgame.exception.LanguageException;
import com.good.animalsgame.extern.api.assembler.CustomMultipartFile;
import com.good.animalsgame.extern.api.controller.SecondRoundLevelController;
import com.good.animalsgame.extern.api.dto.level.SecondRoundLevelDTO;
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
public class SecondRoundLevelAssembler extends RepresentationModelAssemblerSupport<SecondRoundLevel, SecondRoundLevelDTO> {

    private final AnimalService animalService;

    public SecondRoundLevelAssembler(AnimalService animalService) {
        super(SecondRoundLevelController.class, SecondRoundLevelDTO.class);
        this.animalService = animalService;
    }

    /**
     * Маппит уровень 2 раунда в DTO на стандартном русском языке
     * @param secondRoundLevel уровень 2 раунда
     */
    @Override
    public @NonNull SecondRoundLevelDTO toModel(@NonNull SecondRoundLevel secondRoundLevel) {
        SecondRoundLevelDTO secondRoundLevelDTO = instantiateModel(secondRoundLevel);

        secondRoundLevelDTO.setId(secondRoundLevel.getId());
        if (!secondRoundLevel.getAnimals().isEmpty()) {
            secondRoundLevelDTO.setAnimalNames(secondRoundLevel.getAnimals()
                .stream()
                .map(animal -> animal.getNames().get(Language.RUSSIAN))
                .collect(Collectors.toSet()));
        }
        secondRoundLevelDTO.setCorrectAnimalName(secondRoundLevel.getCorrectAnimal().getNames().get(Language.RUSSIAN));
        secondRoundLevelDTO.setAnimalNameInQuestion(secondRoundLevel.getAnimalInQuestion().getNames().get(Language.RUSSIAN));
        secondRoundLevelDTO.setLevelImage(new CustomMultipartFile(secondRoundLevel.getLevelImage(), "image", "image/jpeg"));
        secondRoundLevelDTO.setAnimalCoordinates(secondRoundLevel.getAnimalCoordinates());

        secondRoundLevelDTO.add(linkTo(methodOn(SecondRoundLevelController.class).getLevelById(secondRoundLevel.getId(), "RUSSIAN")).withSelfRel());
        secondRoundLevelDTO.add(linkTo(methodOn(SecondRoundLevelController.class).getRandomLevel("RUSSIAN")).withSelfRel());

        return secondRoundLevelDTO;
    }

    /**
     * Маппит уровень 2 раунда в DTO на выбранном языке
     * @param secondRoundLevel уровень 2 раунда
     * @param language язык
     */
    public @NonNull SecondRoundLevelDTO toModel(@NonNull SecondRoundLevel secondRoundLevel, String language) throws LanguageException, EntityNotFoundException {
        SecondRoundLevelDTO secondRoundLevelDTO = instantiateModel(secondRoundLevel);

        secondRoundLevelDTO.setId(secondRoundLevel.getId());
        if (!secondRoundLevel.getAnimals().isEmpty()) {
            Set<String> animalNames = new HashSet<>();
            for (Animal animal : secondRoundLevel.getAnimals()) {
                animalNames.add(animalService.getAnimalSingleLanguageData(animal.getId(), language).name());
            }

            secondRoundLevelDTO.setAnimalNames(animalNames);
        }
        secondRoundLevelDTO.setCorrectAnimalName(animalService.getAnimalSingleLanguageData(secondRoundLevel.getCorrectAnimal().getId(), language).name());
        secondRoundLevelDTO.setAnimalNameInQuestion(animalService.getAnimalSingleLanguageData(secondRoundLevel.getAnimalInQuestion().getId(), language).name());
        secondRoundLevelDTO.setLevelImage(new CustomMultipartFile(secondRoundLevel.getLevelImage(), "image", "image/jpeg"));
        secondRoundLevelDTO.setAnimalCoordinates(secondRoundLevel.getAnimalCoordinates());

        secondRoundLevelDTO.add(linkTo(methodOn(SecondRoundLevelController.class).getLevelById(secondRoundLevel.getId(), language)).withSelfRel());
        secondRoundLevelDTO.add(linkTo(methodOn(SecondRoundLevelController.class).getRandomLevel(language)).withSelfRel());

        return secondRoundLevelDTO;
    }

    public SecondRoundLevel toEntity(SecondRoundLevelDTO secondRoundLevelDTO) throws EntityNotFoundException, IOException {
        Set<Animal> animals = new HashSet<>();
        for (String animalName : secondRoundLevelDTO.getAnimalNames()) {
            Animal animal = animalService.getAnimalByName(animalName);
            animals.add(animal);
        }

        return SecondRoundLevel.builder()
                .animals(animals)
                .correctAnimal(animalService.getAnimalByName(secondRoundLevelDTO.getCorrectAnimalName()))
                .animalInQuestion(animalService.getAnimalByName(secondRoundLevelDTO.getAnimalNameInQuestion()))
                .levelImage(secondRoundLevelDTO.getLevelImage().getBytes())
                .animalCoordinates(secondRoundLevelDTO.getAnimalCoordinates())
                .build();
    }
}
