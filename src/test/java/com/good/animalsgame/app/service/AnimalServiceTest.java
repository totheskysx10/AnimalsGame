package com.good.animalsgame.app.service;

import com.good.animalsgame.app.repository.AnimalRepository;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.Language;
import com.good.animalsgame.exception.AnimalDuplicateException;
import com.good.animalsgame.exception.AnimalNotFoundException;
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
    void testCreateAnimal() throws AnimalDuplicateException {
        when(animalRepository.findAll()).thenReturn(List.of());

        Animal animal = Animal.builder()
                .id(1L)
                .name(Map.of(Language.RUSSIAN, "Лев"))
                .description(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        animalService.createAnimal(animal);

        verify(animalRepository).save(animal);
    }

    @Test
    void testCreateAnimalDuplicate() {
        Animal animal = Animal.builder()
                .id(1L)
                .name(Map.of(Language.RUSSIAN, "Лев"))
                .description(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        Animal animalInBase = Animal.builder()
                .id(2L)
                .name(Map.of(Language.RUSSIAN, "Лев", Language.ENGLISH, "Lion"))
                .description(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        when(animalRepository.findAll()).thenReturn(new ArrayList<>(List.of(animalInBase)));

        Exception e = assertThrows(AnimalDuplicateException.class, () -> animalService.createAnimal(animal));

        assertEquals("Название животного должно быть уникальным! Даже если хоть по одному из языков есть пересечение, будет ошибка.", e.getMessage());
    }

    @Test
    void testGetAnimalById() throws AnimalNotFoundException {
        Animal animal = Animal.builder()
                .id(1L)
                .name(Map.of(Language.RUSSIAN, "Лев"))
                .description(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        Animal result = animalService.getAnimalById(1L);

        assertEquals("Лев", result.getName().get(Language.RUSSIAN));
    }

    @Test
    void testGetAnimalByIdNotFound() {
        when(animalRepository.findById(1L)).thenReturn(Optional.empty());

        Exception e = assertThrows(AnimalNotFoundException.class, () -> animalService.getAnimalById(1L));

        assertEquals("Животное с id 1 не найдено", e.getMessage());
    }

    @Test
    void testDeleteAnimalById() throws AnimalNotFoundException {
        when(animalRepository.existsById(1L)).thenReturn(true);

        animalService.deleteAnimalById(1L);

        verify(animalRepository).deleteById(1L);
    }

    @Test
    void testAddLanguage() throws AnimalNotFoundException, LanguageException {
        Animal animal = Animal.builder()
                .id(1L)
                .name(new HashMap<>(Map.of(Language.RUSSIAN, "Лев")))
                .description(new HashMap<>(Map.of(Language.RUSSIAN, "Большая кошка")))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        animalService.addLanguage(1L, "ENGLISH", "Lion", "Big cat");

        assertEquals("Lion", animal.getName().get(Language.ENGLISH));
        assertEquals("Big cat", animal.getDescription().get(Language.ENGLISH));
        verify(animalRepository).save(animal);
    }

    @Test
    void testAddLanguageInvalid() {
        Animal animal = Animal.builder()
                .id(1L)
                .name(new HashMap<>(Map.of(Language.RUSSIAN, "Лев")))
                .description(new HashMap<>(Map.of(Language.RUSSIAN, "Большая кошка")))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        Exception e = assertThrows(LanguageException.class, () -> animalService.addLanguage(1L, "INVALID", "Name", "Desc"));

        assertEquals("Нет языка INVALID", e.getMessage());
    }

    @Test
    void testAddLanguageAlreadyExists() {
        Animal animal = Animal.builder()
                .id(1L)
                .name(new HashMap<>(Map.of(Language.RUSSIAN, "Лев")))
                .description(new HashMap<>(Map.of(Language.RUSSIAN, "Большая кошка")))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        Exception e = assertThrows(LanguageException.class, () -> animalService.addLanguage(1L, "RUSSIAN", "Новое имя", "Новое описание"));

        assertEquals("Язык RUSSIAN уже есть!", e.getMessage());
    }

    @Test
    void testRemoveLanguage() throws AnimalNotFoundException, LanguageException {
        Animal animal = Animal.builder()
                .id(1L)
                .name(new HashMap<>(Map.of(Language.RUSSIAN, "Лев", Language.ENGLISH, "Lion")))
                .description(new HashMap<>(Map.of(Language.RUSSIAN, "Большая кошка", Language.ENGLISH, "Big cat")))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        animalService.removeLanguage(1L, "ENGLISH");

        assertFalse(animal.getName().containsKey(Language.ENGLISH));
        assertFalse(animal.getDescription().containsKey(Language.ENGLISH));
        verify(animalRepository).save(animal);
    }

    @Test
    void testRemoveLanguageInvalid() {
        Animal animal = Animal.builder()
                .id(1L)
                .name(new HashMap<>(Map.of(Language.RUSSIAN, "Лев")))
                .description(new HashMap<>(Map.of(Language.RUSSIAN, "Большая кошка")))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        Exception e = assertThrows(LanguageException.class, () -> animalService.removeLanguage(1L, "INVALID"));

        assertEquals("Нет языка INVALID", e.getMessage());
    }

    @Test
    void testRemoveLanguageNotExists() {
        Animal animal = Animal.builder()
                .id(1L)
                .name(new HashMap<>(Map.of(Language.RUSSIAN, "Лев")))
                .description(new HashMap<>(Map.of(Language.RUSSIAN, "Большая кошка")))
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        Exception e = assertThrows(LanguageException.class, () -> animalService.removeLanguage(1L, "ENGLISH"));

        assertEquals("Язык ENGLISH отсутствует!", e.getMessage());
    }
}