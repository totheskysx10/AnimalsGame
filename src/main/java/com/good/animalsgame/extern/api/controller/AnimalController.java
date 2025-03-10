package com.good.animalsgame.extern.api.controller;

import com.good.animalsgame.app.service.AnimalService;
import com.good.animalsgame.exception.AnimalDuplicateException;
import com.good.animalsgame.exception.AnimalNotFoundException;
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

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @Operation(summary = "Создать животное", description = "Создает животное")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Животное успешно создано"),
            @ApiResponse(responseCode = "409", description = "Животное с таким названием уже есть")
    })
    @Transactional
    @PostMapping
    public ResponseEntity<Object> createAnimal(@RequestBody @Valid AnimalDTO animalDTO) {
        try {
            String animalName = animalDTO.getName();
            animalService.createAnimal(animalName);
            return new ResponseEntity<>(new AnimalDTO(animalName), HttpStatus.CREATED);
        } catch (AnimalDuplicateException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Найти животное по названию", description = "Находит животное по названию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Животное успешно найдено"),
            @ApiResponse(responseCode = "404", description = "Животное не найдено")
    })
    @GetMapping("/{name}")
    public ResponseEntity<AnimalDTO> getAnimalByName(@PathVariable String name) {
        try {
            animalService.getAnimalByName(name);
            return ResponseEntity.ok(new AnimalDTO(name));
        } catch (AnimalNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Удалить животное по названию", description = "Удаляет животное по названию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Животное успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Животное не найдено")
    })
    @Transactional
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable String name) {
        try {
            animalService.deleteAnimalByName(name);
            return ResponseEntity.ok().build();
        } catch (AnimalNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
