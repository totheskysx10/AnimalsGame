package com.good.animalsgame.extern.api.controller;

import com.good.animalsgame.app.service.AnimalService;
import com.good.animalsgame.app.service.SecondRoundLevelService;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.SecondRoundLevel;
import com.good.animalsgame.exception.*;
import com.good.animalsgame.extern.api.assembler.SecondRoundLevelAssembler;
import com.good.animalsgame.extern.api.dto.BooleanUserAnswerDTO;
import com.good.animalsgame.extern.api.dto.ErrorDTO;
import com.good.animalsgame.extern.api.dto.SecondRoundLevelDTO;
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
@RequestMapping("/second-round")
public class SecondRoundLevelController {

    private final SecondRoundLevelAssembler secondRoundLevelAssembler;
    private final SecondRoundLevelService secondRoundLevelService;
    private final AnimalService animalService;

    private static final int SECOND_ROUND_NUMBER = 2;

    public SecondRoundLevelController(SecondRoundLevelAssembler secondRoundLevelAssembler,
                                      SecondRoundLevelService secondRoundLevelService,
                                      AnimalService animalService) {
        this.secondRoundLevelAssembler = secondRoundLevelAssembler;
        this.secondRoundLevelService = secondRoundLevelService;
        this.animalService = animalService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Object> createSecondRoundLevel(@RequestBody @Valid SecondRoundLevelDTO secondRoundLevelDTO) {
        try {
            List<Animal> animals = new ArrayList<>();
            for (String animalName : secondRoundLevelDTO.getAnimalNames()) {
                Animal animal = animalService.getAnimalByName(animalName);
                if (animal == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                animals.add(animal);
            }

            SecondRoundLevel secondRoundLevel = SecondRoundLevel.builder()
                    .imageWithAnimal(secondRoundLevelDTO.getImageWithAnimal())
                    .animals(animals)
                    .correctAnimal(animalService.getAnimalByName(secondRoundLevelDTO.getCorrectAnimalName()))
                    .animalInQuestion(animalService.getAnimalByName(secondRoundLevelDTO.getAnimalNameInQuestion()))
                    .build();

            secondRoundLevelService.createLevel(secondRoundLevel);

            return new ResponseEntity<>(secondRoundLevelAssembler.toModel(secondRoundLevel), HttpStatus.CREATED);
        } catch (IncorrectLevelException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (AnimalNotFoundException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SecondRoundLevelDTO> getLevelById(@PathVariable long id) {
        try {
            SecondRoundLevel secondRoundLevel = secondRoundLevelService.getLevelById(id);
            return ResponseEntity.ok(secondRoundLevelAssembler.toModel(secondRoundLevel));
        } catch (LevelNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSecondRoundLevel(@PathVariable long id) {
        try {
            secondRoundLevelService.deleteLevel(id);
            return ResponseEntity.ok().build();
        } catch (LevelNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/random-level")
    public ResponseEntity<SecondRoundLevelDTO> getRandomLevel() {
        try {
            SecondRoundLevel secondRoundLevel = secondRoundLevelService.getRandomLevel(SECOND_ROUND_NUMBER);
            return ResponseEntity.ok(secondRoundLevelAssembler.toModel(secondRoundLevel));
        } catch (LevelNotFoundException | NoSuchRoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoLevelsLeftException e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/is-correct-answer/{id}")
    public ResponseEntity<Map<String, Boolean>> isCorrectAnswer(@PathVariable long id, @RequestBody StringUserAnswerDTO userAnswer) {
        try {
            Boolean isCorrect = secondRoundLevelService.isCorrectAnswer(id, userAnswer.getAnswer());
            Map<String, Boolean> response = new HashMap<>();
            response.put("isCorrect", isCorrect);
            return ResponseEntity.ok(response);
        } catch (LevelNotFoundException | AnimalNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/is-correct-yes-no-answer/{id}")
    public ResponseEntity<Map<String, Boolean>> isCorrectYesNoAnswer(@PathVariable long id, @RequestBody BooleanUserAnswerDTO userAnswer) {
        try {
            Boolean isCorrect = secondRoundLevelService.isCorrectYesNoAnswer(id, userAnswer.getAnswer());
            Map<String, Boolean> response = new HashMap<>();
            response.put("isCorrect", isCorrect);
            return ResponseEntity.ok(response);
        } catch (LevelNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
