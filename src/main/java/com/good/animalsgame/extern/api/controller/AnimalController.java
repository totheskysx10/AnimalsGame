package com.good.animalsgame.extern.api.controller;

import com.good.animalsgame.app.service.AnimalService;
import com.good.animalsgame.exception.AnimalDuplicateException;
import com.good.animalsgame.exception.AnimalNotFoundException;
import com.good.animalsgame.extern.api.dto.AnimalDTO;
import com.good.animalsgame.extern.api.dto.ErrorDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/animals")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

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

    @GetMapping("/{name}")
    public ResponseEntity<AnimalDTO> getAnimalByName(@PathVariable String name) {
        try {
            animalService.getAnimalByName(name);
            return ResponseEntity.ok(new AnimalDTO(name));
        } catch (AnimalNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

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
