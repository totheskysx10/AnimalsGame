package com.good.animalsgame.app.service;

import com.good.animalsgame.app.repository.AnimalRepository;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.exception.AnimalDuplicateException;
import com.good.animalsgame.exception.AnimalNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private AnimalService animalService;

    @Test
    void testCreateAnimal() throws AnimalDuplicateException {
        when(animalRepository.existsByName("Лев")).thenReturn(false);

        animalService.createAnimal("лев", "Большая кошка");

        verify(animalRepository).save(any(Animal.class));
    }

    @Test
    void testCreateAnimalDuplicate() {
        when(animalRepository.existsByName("Лев")).thenReturn(true);

        Exception e = assertThrows(AnimalDuplicateException.class, () -> animalService.createAnimal("лев", "Описание"));

        assertEquals("Название животного должно быть уникальным!", e.getMessage());
    }

    @Test
    void testGetAnimalByName() throws AnimalNotFoundException {
        Animal mockAnimal = new Animal("Лев", "Описание", null, null, null);
        when(animalRepository.findByName("Лев")).thenReturn(Optional.of(mockAnimal));

        Animal result = animalService.getAnimalByName("лев");

        assertEquals("Лев", result.getName());
    }

    @Test
    void testGetAnimalByNamNotFound() {
        when(animalRepository.findByName("Лев")).thenReturn(Optional.empty());

        Exception e = assertThrows(AnimalNotFoundException.class, () -> animalService.getAnimalByName("лев"));

        assertEquals("Животное с названием лев не найдено", e.getMessage());
    }

    @Test
    void testDeleteAnimalByName() throws AnimalNotFoundException {
        when(animalRepository.existsByName("Лев")).thenReturn(true);

        animalService.deleteAnimalByName("лев");

        verify(animalRepository).deleteByName("Лев");
    }
}