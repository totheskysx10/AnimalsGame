package com.good.animalsgame.extern.api.controller;

import com.good.animalsgame.app.service.FirstRoundLevelService;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.FirstRoundLevel;
import com.good.animalsgame.exception.*;
import com.good.animalsgame.extern.api.assembler.AnimalAssembler;
import com.good.animalsgame.extern.api.assembler.level.FirstRoundLevelAssembler;
import com.good.animalsgame.extern.api.dto.AnimalDTO;
import com.good.animalsgame.extern.api.dto.ErrorDTO;
import com.good.animalsgame.extern.api.dto.level.FirstRoundLevelDTO;
import com.good.animalsgame.extern.api.dto.LongUserAnswerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/first-round")
@Tag(name = "FirstRoundLevelController", description = "Контроллер для управления уровнями 1 раунда")
public class FirstRoundLevelController {

    private final FirstRoundLevelAssembler firstRoundLevelAssembler;
    private final FirstRoundLevelService firstRoundLevelService;
    private final AnimalAssembler animalAssembler;

    private static final int FIRST_ROUND_NUMBER = 1;

    public FirstRoundLevelController(FirstRoundLevelAssembler firstRoundLevelAssembler,
                                     FirstRoundLevelService firstRoundLevelService,
                                     AnimalAssembler animalAssembler) {
        this.firstRoundLevelAssembler = firstRoundLevelAssembler;
        this.firstRoundLevelService = firstRoundLevelService;
        this.animalAssembler = animalAssembler;
    }

    @Operation(summary = "Создать уровень 1 раунда", description = "Создает уровень 1 раунда")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Уровень успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос (конкретная ошибка указывается в ответе)"),
            @ApiResponse(responseCode = "404", description = "Не найдено животное, которое упоминается в теле запроса")
    })
    @PostMapping
    @Transactional
    public ResponseEntity<Object> createFirstRoundLevel(@RequestPart("levelImage") MultipartFile levelImage,
                                                        @RequestPart("levelData") @Valid FirstRoundLevelDTO firstRoundLevelDTO) {
        try {
            firstRoundLevelDTO.setLevelImage(levelImage);
            FirstRoundLevel firstRoundLevel = firstRoundLevelAssembler.toEntity(firstRoundLevelDTO);
            firstRoundLevelService.createLevel(firstRoundLevel);

            return new ResponseEntity<>(firstRoundLevelAssembler.toModel(firstRoundLevel), HttpStatus.CREATED);
        } catch (IncorrectLevelException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Получить уровень 1 раунда по ID", description = "Находит по идентификатору уровень 1 раунда")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уровень успешно найден"),
            @ApiResponse(responseCode = "404", description = "Уровень не найден или ошибка поиска языка")
    })
    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<Object> getLevelById(@PathVariable long id, @RequestParam String language) {
        try {
            FirstRoundLevel firstRoundLevel = firstRoundLevelService.getLevelById(id);
            return ResponseEntity.ok(firstRoundLevelAssembler.toModel(firstRoundLevel, language));
        } catch (EntityNotFoundException | LanguageException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Удалить уровень 1 раунда по ID", description = "Удаляет по идентификатору уровень 1 раунда")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уровень успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Уровень не найден")
    })
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFirstRoundLevel(@PathVariable long id) {
        try {
            firstRoundLevelService.deleteLevel(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получить рандомный уровень 1 раунда", description = "Возвращает рандомный уровень 1 раунда")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уровень успешно найден"),
            @ApiResponse(responseCode = "404", description = "Внутренняя ошибка поиска уровня или отсутствует язык у уровня или в целом"),
            @ApiResponse(responseCode = "204", description = "Все уровни были показаны - больше нечего возвращать")
    })
    @GetMapping("/random-level")
    public ResponseEntity<Object> getRandomLevel(@RequestParam String language) {
        try {
            FirstRoundLevel firstRoundLevel = firstRoundLevelService.getRandomLevel(FIRST_ROUND_NUMBER);
            return ResponseEntity.ok(firstRoundLevelAssembler.toModel(firstRoundLevel, language));
        } catch (EntityNotFoundException | NoSuchRoundException | LanguageException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (NoLevelsLeftException e) {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "Проверить корректность ответа пользователя на вопрос с выбором животного", description = "Проверяет, верно ли ответил пользователь")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "true, если пользователь ответил верно, иначе - false"),
            @ApiResponse(responseCode = "404", description = "Не найдено животное или ошибка поиска уровня")
    })
    @PostMapping("/is-correct-answer/{id}")
    public ResponseEntity<Map<String, Boolean>> isCorrectAnswer(@PathVariable long id, @RequestBody LongUserAnswerDTO userAnswer) {
        try {
            Boolean isCorrect = firstRoundLevelService.isCorrectAnswer(id, userAnswer.getAnswer());
            Map<String, Boolean> response = new HashMap<>();
            response.put("isCorrect", isCorrect);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получить верный ответ уровень 1 раунда", description = "Получает верный ответ уровня 1 раунда")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Корректный возврат"),
            @ApiResponse(responseCode = "404", description = "Уровень не найден")
    })
    @Transactional
    @GetMapping("/{id}/correct")
    public ResponseEntity<AnimalDTO> getLevelCorrectAnimal(@PathVariable long id) {
        try {
            Animal animal = firstRoundLevelService.getLevelCorrectAnimal(id);
            return ResponseEntity.ok(animalAssembler.toModel(animal));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
