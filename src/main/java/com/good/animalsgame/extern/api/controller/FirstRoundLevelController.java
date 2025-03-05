package com.good.animalsgame.extern.api.controller;

import com.good.animalsgame.app.service.AnimalService;
import com.good.animalsgame.app.service.FirstRoundLevelService;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.FirstRoundLevel;
import com.good.animalsgame.exception.*;
import com.good.animalsgame.extern.api.assembler.FirstRoundLevelAssembler;
import com.good.animalsgame.extern.api.dto.ErrorDTO;
import com.good.animalsgame.extern.api.dto.FirstRoundLevelDTO;
import com.good.animalsgame.extern.api.dto.StringUserAnswerDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/first-round")
public class FirstRoundLevelController {

    private final FirstRoundLevelAssembler firstRoundLevelAssembler;
    private final FirstRoundLevelService firstRoundLevelService;
    private final AnimalService animalService;

    private static final int FIRST_ROUND_NUMBER = 1;

    public FirstRoundLevelController(FirstRoundLevelAssembler firstRoundLevelAssembler,
                                     FirstRoundLevelService firstRoundLevelService,
                                     AnimalService animalService) {
        this.firstRoundLevelAssembler = firstRoundLevelAssembler;
        this.firstRoundLevelService = firstRoundLevelService;
        this.animalService = animalService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Object> createFirstRoundLevel(@RequestBody @Valid FirstRoundLevelDTO firstRoundLevelDTO) {
        try {
            List<Animal> animals = new ArrayList<>();
            for (String animalName : firstRoundLevelDTO.getAnimalNames()) {
                Animal animal = animalService.getAnimalByName(animalName);
                if (animal == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                animals.add(animal);
            }

            FirstRoundLevel firstRoundLevel = FirstRoundLevel.builder()
                    .imageWithAnimal(firstRoundLevelDTO.getImageWithAnimal())
                    .animals(animals)
                    .correctAnimal(animalService.getAnimalByName(firstRoundLevelDTO.getCorrectAnimalName()))
                    .levelImage(firstRoundLevelDTO.getLevelImage())
                    .animalCoordinates(firstRoundLevelDTO.getAnimalCoordinates())
                    .build();

            firstRoundLevelService.createLevel(firstRoundLevel);

            return new ResponseEntity<>(firstRoundLevelAssembler.toModel(firstRoundLevel), HttpStatus.CREATED);
        } catch (IncorrectLevelException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (AnimalNotFoundException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FirstRoundLevelDTO> getLevelById(@PathVariable long id) {
        try {
            FirstRoundLevel firstRoundLevel = firstRoundLevelService.getLevelById(id);
            return ResponseEntity.ok(firstRoundLevelAssembler.toModel(firstRoundLevel));
        } catch (LevelNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFirstRoundLevel(@PathVariable long id) {
        try {
            firstRoundLevelService.deleteLevel(id);
            return ResponseEntity.ok().build();
        } catch (LevelNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/random-level")
    public ResponseEntity<FirstRoundLevelDTO> getRandomLevel() {
        try {
            FirstRoundLevel firstRoundLevel = firstRoundLevelService.getRandomLevel(FIRST_ROUND_NUMBER);
            return ResponseEntity.ok(firstRoundLevelAssembler.toModel(firstRoundLevel));
        } catch (LevelNotFoundException | NoSuchRoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoLevelsLeftException e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/is-correct-answer/{id}")
    public ResponseEntity<Map<String, Boolean>> isCorrectAnswer(@PathVariable long id, @RequestBody StringUserAnswerDTO userAnswer) {
        try {
            Boolean isCorrect = firstRoundLevelService.isCorrectAnswer(id, userAnswer.getAnswer());
            Map<String, Boolean> response = new HashMap<>();
            response.put("isCorrect", isCorrect);
            return ResponseEntity.ok(response);
        } catch (LevelNotFoundException | AnimalNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
