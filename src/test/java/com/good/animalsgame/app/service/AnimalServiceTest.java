package com.good.animalsgame.app.service;

import com.good.animalsgame.app.repository.AnimalRepository;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.Language;
import com.good.animalsgame.exception.EntityDuplicateException;
import com.good.animalsgame.exception.EntityNotFoundException;
import com.good.animalsgame.exception.LanguageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private AnimalService animalService;

    @Test
    void testCreateAnimal() throws EntityDuplicateException {
        when(animalRepository.findByName("Лев")).thenReturn(Optional.empty());

        Animal animal = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        animalService.createAnimal(animal);

        verify(animalRepository).save(animal);
    }

    @Test
    void testCreateAnimalDuplicate() {
        Animal animal = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        Animal animalInBase = Animal.builder()
                .id(2L)
                .names(Map.of(Language.RUSSIAN, "Лев", Language.ENGLISH, "Lion"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        when(animalRepository.findByName("Лев")).thenReturn(Optional.of(animalInBase));

        Exception e = assertThrows(EntityDuplicateException.class, () -> animalService.createAnimal(animal));

        assertEquals("Название животного должно быть уникальным! Даже если хоть по одному из языков есть пересечение, будет ошибка.", e.getMessage());
    }

    @Test
    void testGetAnimalById() throws EntityNotFoundException {
        Animal animal = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        Animal result = animalService.getAnimalById(1L);

        assertEquals("Лев", result.getNames().get(Language.RUSSIAN));
    }

    @Test
    void testGetAnimalByName() throws EntityNotFoundException {
        Animal animal = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев", Language.ENGLISH, "Lion"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка", Language.ENGLISH, "A big cat"))
                .build();

        when(animalRepository.findByName("Лев")).thenReturn(Optional.of(animal));

        Animal result = animalService.getAnimalByName("Лев");

        assertEquals("Лев", result.getNames().get(Language.RUSSIAN));
    }

    @Test
    void testGetAnimalByIdNotFound() {
        when(animalRepository.findById(1L)).thenReturn(Optional.empty());

        Exception e = assertThrows(EntityNotFoundException.class, () -> animalService.getAnimalById(1L));

        assertEquals("Животное с id 1 не найдено", e.getMessage());
    }

    @Test
    void testGetAnimalByNameNotFound() {
        when(animalRepository.findByName("Лев")).thenReturn(Optional.empty());

        Exception e = assertThrows(EntityNotFoundException.class, () -> animalService.getAnimalByName("Лев"));

        assertEquals("Животное с названием Лев не найдено", e.getMessage());
    }

    @Test
    void testDeleteAnimalById() throws EntityNotFoundException {
        when(animalRepository.existsById(1L)).thenReturn(true);

        animalService.deleteAnimalById(1L);

        verify(animalRepository).deleteById(1L);
    }

    @Test
    void testGetAnimalSingleLanguageData() throws EntityNotFoundException, LanguageException {
        Animal animal = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев", Language.ENGLISH, "Lion"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка", Language.ENGLISH, "A big cat"))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        AnimalSingleLanguageData animalSingleLanguageData = animalService.getAnimalSingleLanguageData(1L, "ENGLISH");

        assertEquals("Lion", animalSingleLanguageData.name());
        assertEquals("A big cat", animalSingleLanguageData.description());
    }

    @Test
    void testGetAnimalSingleLanguageDataInvalid() {
        Animal animal = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев", Language.ENGLISH, "Lion"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка", Language.ENGLISH, "A big cat"))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        Exception e = assertThrows(LanguageException.class, () -> animalService.getAnimalSingleLanguageData(1L, "INVALID"));

        assertEquals("Нет языка INVALID", e.getMessage());
    }

    @Test
    void testGetAnimalSingleLanguageDataExists() {
        Animal animal = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев", Language.ENGLISH, "Lion"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка", Language.ENGLISH, "A big cat"))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        Exception e = assertThrows(LanguageException.class, () -> animalService.getAnimalSingleLanguageData(1L, "ITALIAN"));

        assertEquals("Язык ITALIAN отсутствует у животного!", e.getMessage());
    }

    @Test
    void testAddLanguage() throws EntityNotFoundException, LanguageException {
        Animal animal = Animal.builder()
                .id(1L)
                .names(new HashMap<>(Map.of(Language.RUSSIAN, "Лев")))
                .descriptions(new HashMap<>(Map.of(Language.RUSSIAN, "Большая кошка")))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        AnimalSingleLanguageData animalSingleLanguageData = new AnimalSingleLanguageData("Lion", "Big cat");
        animalService.addLanguage(1L, "ENGLISH", animalSingleLanguageData);

        assertEquals("Lion", animal.getNames().get(Language.ENGLISH));
        assertEquals("Big cat", animal.getDescriptions().get(Language.ENGLISH));
        verify(animalRepository).save(animal);
    }

    @Test
    void testAddLanguageInvalid() {
        Animal animal = Animal.builder()
                .id(1L)
                .names(new HashMap<>(Map.of(Language.RUSSIAN, "Лев")))
                .descriptions(new HashMap<>(Map.of(Language.RUSSIAN, "Большая кошка")))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        AnimalSingleLanguageData animalSingleLanguageData = new AnimalSingleLanguageData("Lion", "Big cat");
        Exception e = assertThrows(LanguageException.class, () -> animalService.addLanguage(1L, "INVALID", animalSingleLanguageData));

        assertEquals("Нет языка INVALID", e.getMessage());
    }

    @Test
    void testAddLanguageAlreadyExists() {
        Animal animal = Animal.builder()
                .id(1L)
                .names(new HashMap<>(Map.of(Language.RUSSIAN, "Лев")))
                .descriptions(new HashMap<>(Map.of(Language.RUSSIAN, "Большая кошка")))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        AnimalSingleLanguageData animalSingleLanguageData = new AnimalSingleLanguageData("Lion", "Big cat");
        Exception e = assertThrows(LanguageException.class, () -> animalService.addLanguage(1L, "RUSSIAN", animalSingleLanguageData));

        assertEquals("Язык RUSSIAN уже есть!", e.getMessage());
    }

    @Test
    void testRemoveLanguage() throws EntityNotFoundException, LanguageException {
        Animal animal = Animal.builder()
                .id(1L)
                .names(new HashMap<>(Map.of(Language.RUSSIAN, "Лев", Language.ENGLISH, "Lion")))
                .descriptions(new HashMap<>(Map.of(Language.RUSSIAN, "Большая кошка", Language.ENGLISH, "Big cat")))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        animalService.removeLanguage(1L, "ENGLISH");

        assertFalse(animal.getNames().containsKey(Language.ENGLISH));
        assertFalse(animal.getDescriptions().containsKey(Language.ENGLISH));
        verify(animalRepository).save(animal);
    }

    @Test
    void testRemoveLanguageInvalid() {
        Animal animal = Animal.builder()
                .id(1L)
                .names(new HashMap<>(Map.of(Language.RUSSIAN, "Лев")))
                .descriptions(new HashMap<>(Map.of(Language.RUSSIAN, "Большая кошка")))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        Exception e = assertThrows(LanguageException.class, () -> animalService.removeLanguage(1L, "INVALID"));

        assertEquals("Нет языка INVALID", e.getMessage());
    }

    @Test
    void testRemoveLanguageNotExists() {
        Animal animal = Animal.builder()
                .id(1L)
                .names(new HashMap<>(Map.of(Language.RUSSIAN, "Лев")))
                .descriptions(new HashMap<>(Map.of(Language.RUSSIAN, "Большая кошка")))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        Exception e = assertThrows(LanguageException.class, () -> animalService.removeLanguage(1L, "ENGLISH"));

        assertEquals("Язык ENGLISH отсутствует у животного!", e.getMessage());
    }

    @Test
    void testRemoveLanguageLast() {
        Animal animal = Animal.builder()
                .id(1L)
                .names(new HashMap<>(Map.of(Language.RUSSIAN, "Лев")))
                .descriptions(new HashMap<>(Map.of(Language.RUSSIAN, "Большая кошка")))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        Exception e = assertThrows(LanguageException.class, () -> animalService.removeLanguage(1L, "RUSSIAN"));

        assertEquals("Невозможно удалить последний язык!", e.getMessage());
    }
}