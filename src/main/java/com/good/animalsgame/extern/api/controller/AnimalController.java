package com.good.animalsgame.extern.api.controller;

import com.good.animalsgame.app.service.AnimalService;
import com.good.animalsgame.app.service.AnimalSingleLanguageData;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.exception.EntityDuplicateException;
import com.good.animalsgame.exception.EntityNotFoundException;
import com.good.animalsgame.exception.LanguageException;
import com.good.animalsgame.extern.api.assembler.AnimalAssembler;
import com.good.animalsgame.extern.api.dto.AddAnimalLanguageDTO;
import com.good.animalsgame.extern.api.dto.AnimalDTO;
import com.good.animalsgame.extern.api.dto.ErrorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/animals")
@Tag(name = "AnimalController", description = "Контроллер для управления животными")
public class AnimalController {

    private final AnimalService animalService;
    private final AnimalAssembler animalAssembler;

    public AnimalController(AnimalService animalService, AnimalAssembler animalAssembler) {
        this.animalService = animalService;
        this.animalAssembler = animalAssembler;
    }

    @Operation(summary = "Создать животное", description = "Создает животное")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Животное успешно создано"),
            @ApiResponse(responseCode = "409", description = "Животное с таким названием уже есть"),
            @ApiResponse(responseCode = "404", description = "Ошибка языка")
    })
    @Transactional
    @PostMapping
    public ResponseEntity<Object> createAnimal(@RequestBody @Valid AnimalDTO animalDTO) {
        try {
            Animal animal = animalAssembler.toEntity(animalDTO);
            animalService.createAnimal(animal);

            return new ResponseEntity<>(animalAssembler.toModel(animal), HttpStatus.CREATED);
        } catch (EntityDuplicateException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.CONFLICT);
        } catch (LanguageException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Найти животное по Id", description = "Находит животное по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Животное успешно найдено"),
            @ApiResponse(responseCode = "404", description = "Животное не найдено")
    })
    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<AnimalDTO> getAnimalById(@PathVariable Long id) {
        try {
            Animal animal = animalService.getAnimalById(id);
            return ResponseEntity.ok(animalAssembler.toModel(animal));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Удалить животное по ID", description = "Удаляет животное по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Животное успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Животное не найдено")
    })
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        try {
            animalService.deleteAnimalById(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Добавить язык животному по ID", description = "Добавляет язык животному по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Язык успешно добавлен"),
            @ApiResponse(responseCode = "404", description = "Животное не найдено"),
            @ApiResponse(responseCode = "409", description = "Дубликат языков")
    })
    @Transactional
    @PutMapping("/language/add/{id}")
    public ResponseEntity<Object> addLanguage(@PathVariable Long id, @RequestParam String language,
                                              @RequestBody @Valid AddAnimalLanguageDTO addAnimalLanguageDTO) {
        try {
            AnimalSingleLanguageData animalSingleLanguageData =
                    new AnimalSingleLanguageData(addAnimalLanguageDTO.getName(), addAnimalLanguageDTO.getDescription());
            animalService.addLanguage(id, language, animalSingleLanguageData);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (LanguageException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO(e.getMessage()));
        }
    }

    @Operation(summary = "Убрать язык животному по ID", description = "Убирает язык животному по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Язык успешно убран"),
            @ApiResponse(responseCode = "404", description = "Животное не найдено"),
            @ApiResponse(responseCode = "409", description = "Языка не было")
    })
    @Transactional
    @PutMapping("/language/remove/{id}")
    public ResponseEntity<Object> removeLanguage(@PathVariable Long id, @RequestParam String language) {
        try {
            animalService.removeLanguage(id, language);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (LanguageException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO(e.getMessage()));
        }
    }
}
