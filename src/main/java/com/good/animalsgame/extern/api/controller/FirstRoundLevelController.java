package com.good.animalsgame.extern.api.controller;

import com.good.animalsgame.app.service.FirstRoundLevelService;
import com.good.animalsgame.domain.FirstRoundLevel;
import com.good.animalsgame.exception.*;
import com.good.animalsgame.extern.api.assembler.FirstRoundLevelAssembler;
import com.good.animalsgame.extern.api.dto.ErrorDTO;
import com.good.animalsgame.extern.api.dto.FirstRoundLevelDTO;
import com.good.animalsgame.extern.api.dto.StringUserAnswerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/first-round")
@Tag(name = "FirstRoundLevelController", description = "Контроллер для управления уровнями 1 раунда")
public class FirstRoundLevelController {

    private final FirstRoundLevelAssembler firstRoundLevelAssembler;
    private final FirstRoundLevelService firstRoundLevelService;

    private static final int FIRST_ROUND_NUMBER = 1;

    public FirstRoundLevelController(FirstRoundLevelAssembler firstRoundLevelAssembler,
                                     FirstRoundLevelService firstRoundLevelService) {
        this.firstRoundLevelAssembler = firstRoundLevelAssembler;
        this.firstRoundLevelService = firstRoundLevelService;
    }

    @Operation(summary = "Создать уровень 1 раунда", description = "Создает уровень 1 раунда")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Уровень успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос (конкретная ошибка указывается в ответе)"),
            @ApiResponse(responseCode = "404", description = "Не найдено животное, которое упоминается в теле запроса")
    })
    @PostMapping
    @Transactional
    public ResponseEntity<Object> createFirstRoundLevel(@RequestBody @Valid FirstRoundLevelDTO firstRoundLevelDTO) {
        try {
            FirstRoundLevel firstRoundLevel = firstRoundLevelAssembler.toEntity(firstRoundLevelDTO);
            firstRoundLevelService.createLevel(firstRoundLevel);

            return new ResponseEntity<>(firstRoundLevelAssembler.toModel(firstRoundLevel), HttpStatus.CREATED);
        } catch (IncorrectLevelException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (AnimalNotFoundException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Получить уровень 1 раунда по ID", description = "Находит по идентификатору уровень 1 раунда")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уровень успешно найден"),
            @ApiResponse(responseCode = "404", description = "Уровень не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FirstRoundLevelDTO> getLevelById(@PathVariable long id) {
        try {
            FirstRoundLevel firstRoundLevel = firstRoundLevelService.getLevelById(id);
            return ResponseEntity.ok(firstRoundLevelAssembler.toModel(firstRoundLevel));
        } catch (LevelNotFoundException e) {
            return ResponseEntity.notFound().build();
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
        } catch (LevelNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получить рандомный уровень 1 раунда", description = "Возвращает рандомный уровень 1 раунда")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уровень успешно найден"),
            @ApiResponse(responseCode = "404", description = "Внутренняя ошибка поиска уровня"),
            @ApiResponse(responseCode = "204", description = "Все уровни были показаны - больше нечего возвращать")
    })
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

    @Operation(summary = "Проверить корректность ответа пользователя на вопрос с выбором животного", description = "Проверяет, верно ли ответил пользователь")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "true, если пользователь ответил верно, иначе - false"),
            @ApiResponse(responseCode = "404", description = "Не найдено животное или ошибка поиска уровня")
    })
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
