package com.good.animalsgame.extern.api.controller;

import com.good.animalsgame.app.service.SecondRoundLevelService;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.SecondRoundLevel;
import com.good.animalsgame.exception.*;
import com.good.animalsgame.extern.api.assembler.AnimalAssembler;
import com.good.animalsgame.extern.api.assembler.level.SecondRoundLevelAssembler;
import com.good.animalsgame.extern.api.dto.*;
import com.good.animalsgame.extern.api.dto.level.SecondRoundLevelDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/second-round")
@Tag(name = "SecondRoundLevelController", description = "Контроллер для управления уровнями 2 раунда")
public class SecondRoundLevelController {

    private final SecondRoundLevelAssembler secondRoundLevelAssembler;
    private final SecondRoundLevelService secondRoundLevelService;
    private final AnimalAssembler animalAssembler;

    private static final int SECOND_ROUND_NUMBER = 2;

    public SecondRoundLevelController(SecondRoundLevelAssembler secondRoundLevelAssembler,
                                      SecondRoundLevelService secondRoundLevelService,
                                      AnimalAssembler animalAssembler) {
        this.secondRoundLevelAssembler = secondRoundLevelAssembler;
        this.secondRoundLevelService = secondRoundLevelService;
        this.animalAssembler = animalAssembler;
    }

    @Operation(summary = "Создать уровень 2 раунда", description = "Создает уровень 2 раунда")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Уровень успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос (конкретная ошибка указывается в ответе)"),
            @ApiResponse(responseCode = "404", description = "Не найдено животное, которое упоминается в теле запроса")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<Object> createSecondRoundLevel(@RequestPart("levelImage") MultipartFile levelImage,
                                                         @RequestPart("levelData") @Schema(type = "string", format = "binary") @Valid SecondRoundLevelDTO secondRoundLevelDTO) {
        try {
            secondRoundLevelDTO.setLevelImage(levelImage);
            SecondRoundLevel secondRoundLevel = secondRoundLevelAssembler.toEntity(secondRoundLevelDTO);
            secondRoundLevelService.createLevel(secondRoundLevel);

            return new ResponseEntity<>(secondRoundLevelAssembler.toModel(secondRoundLevel), HttpStatus.CREATED);
        } catch (IncorrectLevelException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Получить уровень 2 раунда по ID", description = "Находит по идентификатору уровень 2 раунда")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уровень успешно найден"),
            @ApiResponse(responseCode = "404", description = "Уровень не найден или ошибка поиска языка")
    })
    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<Object> getLevelById(@PathVariable long id, @RequestParam String language) {
        try {
            SecondRoundLevel secondRoundLevel = secondRoundLevelService.getLevelById(id);
            return ResponseEntity.ok(secondRoundLevelAssembler.toModel(secondRoundLevel, language));
        } catch (EntityNotFoundException | LanguageException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Удалить уровень 2 раунда по ID", description = "Удаляет по идентификатору уровень 2 раунда")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уровень успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Уровень не найден")
    })
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSecondRoundLevel(@PathVariable long id) {
        try {
            secondRoundLevelService.deleteLevel(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получить рандомный уровень 2 раунда", description = "Возвращает рандомный уровень 2 раунда")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уровень успешно найден"),
            @ApiResponse(responseCode = "404", description = "Внутренняя ошибка поиска уровня или отсутствует язык у уровня или в целом"),
            @ApiResponse(responseCode = "204", description = "Все уровни были показаны - больше нечего возвращать")
    })
    @Transactional
    @GetMapping("/random-level")
    public ResponseEntity<Object> getRandomLevel(@RequestParam String language) {
        try {
            SecondRoundLevel secondRoundLevel = secondRoundLevelService.getRandomLevel(SECOND_ROUND_NUMBER);
            return ResponseEntity.ok(secondRoundLevelAssembler.toModel(secondRoundLevel, language));
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
    public ResponseEntity<Map<String, Boolean>> isCorrectAnswer(@PathVariable long id, @RequestBody StringUserAnswerDTO userAnswer) {
        try {
            Boolean isCorrect = secondRoundLevelService.isCorrectAnswer(id, userAnswer.getAnswer());
            Map<String, Boolean> response = new HashMap<>();
            response.put("isCorrect", isCorrect);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Проверить корректность ответа пользователя на вопрос Да/Нет", description = "Проверяет, верно ли ответил пользователь")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "true, если пользователь ответил верно, иначе - false"),
            @ApiResponse(responseCode = "404", description = "Ошибка поиска уровня")
    })
    @PostMapping("/is-correct-yes-no-answer/{id}")
    public ResponseEntity<Map<String, Boolean>> isCorrectYesNoAnswer(@PathVariable long id, @RequestBody BooleanUserAnswerDTO userAnswer) {
        try {
            Boolean isCorrect = secondRoundLevelService.isCorrectYesNoAnswer(id, userAnswer.getAnswer());
            Map<String, Boolean> response = new HashMap<>();
            response.put("isCorrect", isCorrect);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получить верный ответ уровень 2 раунда", description = "Получает верный ответ уровня 2 раунда")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Корректный возврат"),
            @ApiResponse(responseCode = "404", description = "Уровень не найден")
    })
    @Transactional
    @GetMapping("/{id}/correct")
    public ResponseEntity<AnimalDTO> getLevelCorrectAnimal(@PathVariable long id) {
        try {
            Animal animal = secondRoundLevelService.getLevelCorrectAnimal(id);
            return ResponseEntity.ok(animalAssembler.toModel(animal));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
