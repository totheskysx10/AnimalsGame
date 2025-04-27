package com.good.animalsgame.app.service;

import com.good.animalsgame.app.cache.LevelsSessionCache;
import com.good.animalsgame.app.repository.FirstRoundLevelRepository;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.FirstRoundLevel;
import com.good.animalsgame.domain.Language;
import com.good.animalsgame.domain.SecondRoundLevel;
import com.good.animalsgame.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FirstRoundLevelServiceTest {

    @Mock
    private LevelsSessionCache levelsSessionCache;

    @Mock
    private FirstRoundLevelRepository levelRepository;

    @Mock
    private AnimalService animalService;

    @InjectMocks
    private FirstRoundLevelService firstRoundLevelService;

    @Test
    void testCreateLevel() throws IncorrectLevelException {
        Animal animal1 = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();
        Animal animal2 = Animal.builder()
                .id(2L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();
        Animal animal3 = Animal.builder()
                .id(3L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();
        Animal animal4 = Animal.builder()
                .id(4L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        Set<Animal> animalSet = Set.of(animal1, animal2, animal3, animal4);

        FirstRoundLevel level = FirstRoundLevel.builder()
                .animals(animalSet)
                .correctAnimal(animal3)
                .build();

        when(levelRepository.save(level)).thenReturn(level);

        FirstRoundLevel result = firstRoundLevelService.createLevel(level);

        assertNotNull(result);
        verify(levelRepository).save(level);
    }

    @Test
    void testCreateLevelWithIncorrectAnimalSetSize() {
        Animal animal1 = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();
        Animal animal2 = Animal.builder()
                .id(2L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();
        Animal animal3 = Animal.builder()
                .id(3L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        Set<Animal> animalSet = Set.of(animal1, animal2, animal3);

        FirstRoundLevel level = FirstRoundLevel.builder()
                .animals(animalSet)
                .correctAnimal(animal3)
                .build();


        Exception e = assertThrows(IncorrectLevelException.class,
                () -> firstRoundLevelService.createLevel(level));

        assertEquals("Список животных должен содержать ровно 4 элемента. Без дубликатов.", e.getMessage());
    }

    @Test
    void testCreateLevelWithoutCorrectAnimalInSet() {
        Animal animal1 = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();
        Animal animal2 = Animal.builder()
                .id(2L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();
        Animal animal3 = Animal.builder()
                .id(3L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();
        Animal animal4 = Animal.builder()
                .id(4L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();
        Animal animal5 = Animal.builder()
                .id(5L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        Set<Animal> animalSet = Set.of(animal1, animal2, animal3, animal4);

        FirstRoundLevel level = FirstRoundLevel.builder()
                .animals(animalSet)
                .correctAnimal(animal5)
                .build();

        Exception e = assertThrows(IncorrectLevelException.class,
                () -> firstRoundLevelService.createLevel(level));

        assertEquals("Верный ответ должен содержаться в списке!", e.getMessage());
    }

    @Test
    void testGetLevelById() throws EntityNotFoundException {
        Long levelId = 1L;
        FirstRoundLevel level = new FirstRoundLevel();
        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));

        FirstRoundLevel result = firstRoundLevelService.getLevelById(levelId);

        assertNotNull(result);
    }

    @Test
    void testGetLevelByIdNotFound() {
        Long levelId = 1L;
        when(levelRepository.findById(levelId)).thenReturn(Optional.empty());

        Exception e = assertThrows(EntityNotFoundException.class, () -> firstRoundLevelService.getLevelById(levelId));

        assertEquals("Уровень с id 1 не найден", e.getMessage());
    }

    @Test
    void testDeleteLevel() throws EntityNotFoundException {
        Long levelId = 1L;
        when(levelRepository.existsById(levelId)).thenReturn(true);

        firstRoundLevelService.deleteLevel(levelId);

        verify(levelRepository).deleteById(levelId);
    }

    @Test
    void testGetRandomLevel() throws EntityNotFoundException, NoSuchRoundException, NoLevelsLeftException {
        int round = 1;
        Long levelId = 1L;
        FirstRoundLevel level = new FirstRoundLevel();

        when(levelsSessionCache.getRoundSize(round)).thenReturn(1);
        when(levelsSessionCache.getLevelId(round, 0)).thenReturn(levelId);
        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));

        FirstRoundLevel result = firstRoundLevelService.getRandomLevel(round);

        assertNotNull(result);
        verify(levelsSessionCache).removeLevel(round, 0);
    }

    @Test
    void testGetRandomLevelNoLevelsLeft() throws NoSuchRoundException {
        int round = 1;
        when(levelsSessionCache.getRoundSize(round)).thenReturn(0);

        Exception e = assertThrows(NoLevelsLeftException.class, () -> firstRoundLevelService.getRandomLevel(round));

        assertEquals("Уровней в раунде 1 больше нет", e.getMessage());
    }

    @Test
    void testIsCorrectAnswer() throws EntityNotFoundException, EntityNotFoundException {
        Long levelId = 1L;
        Long userAnswer = 1L;
        Animal correctAnimal = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        FirstRoundLevel level = FirstRoundLevel.builder()
                .correctAnimal(correctAnimal)
                .build();

        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));
        when(animalService.getAnimalById(userAnswer)).thenReturn(correctAnimal);

        assertTrue(firstRoundLevelService.isCorrectAnswer(levelId, userAnswer));
    }

    @Test
    void testGetLevelCorrectAnimal() throws EntityNotFoundException {
        Long levelId = 1L;
        Animal correctAnimal = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        FirstRoundLevel level = FirstRoundLevel.builder()
                .correctAnimal(correctAnimal)
                .build();

        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));

        Animal result = firstRoundLevelService.getLevelCorrectAnimal(levelId);

        assertEquals(correctAnimal, result);
    }
}