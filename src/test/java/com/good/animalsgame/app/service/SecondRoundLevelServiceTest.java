package com.good.animalsgame.app.service;

import com.good.animalsgame.app.cache.LevelsSessionCache;
import com.good.animalsgame.app.repository.SecondRoundLevelRepository;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.Language;
import com.good.animalsgame.domain.SecondRoundLevel;
import com.good.animalsgame.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecondRoundLevelServiceTest {

    @Mock
    private LevelsSessionCache levelsSessionCache;

    @Mock
    private SecondRoundLevelRepository levelRepository;

    @Mock
    private AnimalService animalService;

    @InjectMocks
    private SecondRoundLevelService secondRoundLevelService;

    @Test
    void testCreateLevelAnswerMatchQuestion() throws IncorrectLevelException {
        Animal animal = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        SecondRoundLevel level = SecondRoundLevel.builder()
                .animalInQuestion(animal)
                .correctAnimal(animal)
                .animals(Collections.emptySet())
                .build();

        when(levelRepository.save(level)).thenReturn(level);

        SecondRoundLevel result = secondRoundLevelService.createLevel(level);

        assertNotNull(result);
        verify(levelRepository).save(level);
    }

    @Test
    void testCreateLevelAnswerNotMatchQuestion() throws IncorrectLevelException {
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

        SecondRoundLevel level = SecondRoundLevel.builder()
                .animalInQuestion(animal2)
                .correctAnimal(animal3)
                .animals(Set.of(animal1, animal2, animal3, animal4))
                .build();

        when(levelRepository.save(level)).thenReturn(level);

        SecondRoundLevel result = secondRoundLevelService.createLevel(level);

        assertNotNull(result);
        verify(levelRepository).save(level);
    }

    @Test
    void testCreateLevelAnswerMatchAnimalsNotEmpty() {
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

        SecondRoundLevel level = SecondRoundLevel.builder()
                .animalInQuestion(animal3)
                .correctAnimal(animal3)
                .animals(Set.of(animal1, animal2, animal3, animal4))
                .build();

        Exception e = assertThrows(IncorrectLevelException.class, () -> secondRoundLevelService.createLevel(level));

        assertEquals("Список животных должен быть пустым при совпадении верного ответа и вопроса", e.getMessage());
    }

    @Test
    void testCreateLevelAnswerNotMatchAnimalsEmpty() {
        Animal questionAnimal = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        Animal correctAnimal = Animal.builder()
                .id(2L)
                .names(Map.of(Language.RUSSIAN, "Тигр"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        SecondRoundLevel level = SecondRoundLevel.builder()
                .animalInQuestion(questionAnimal)
                .correctAnimal(correctAnimal)
                .animals(Collections.emptySet())
                .build();

        Exception e = assertThrows(IncorrectLevelException.class, () -> secondRoundLevelService.createLevel(level));

        assertEquals("Список животных не должен быть пустым при НЕ совпадении верного ответа и вопроса", e.getMessage());
    }

    @Test
    void testIsCorrectYesNoAnswerMatchQuestion() throws EntityNotFoundException {
        Long levelId = 1L;
        Animal animal = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        SecondRoundLevel level = SecondRoundLevel.builder()
                .animalInQuestion(animal)
                .correctAnimal(animal)
                .build();

        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));

        assertTrue(secondRoundLevelService.isCorrectYesNoAnswer(levelId, true));
        assertFalse(secondRoundLevelService.isCorrectYesNoAnswer(levelId, false));
    }

    @Test
    void testIsCorrectYesNoAnswerNotMatchQuestion() throws EntityNotFoundException {
        Long levelId = 1L;
        Animal questionAnimal = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        Animal correctAnimal = Animal.builder()
                .id(2L)
                .names(Map.of(Language.RUSSIAN, "Тигр"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        SecondRoundLevel level = SecondRoundLevel.builder()
                .animalInQuestion(questionAnimal)
                .correctAnimal(correctAnimal)
                .build();

        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));

        assertFalse(secondRoundLevelService.isCorrectYesNoAnswer(levelId, true));
        assertTrue(secondRoundLevelService.isCorrectYesNoAnswer(levelId, false));
    }

    @Test
    void testGetLevelById() throws EntityNotFoundException {
        Long levelId = 1L;
        SecondRoundLevel level = new SecondRoundLevel();
        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));

        SecondRoundLevel result = secondRoundLevelService.getLevelById(levelId);

        assertNotNull(result);
    }

    @Test
    void testGetLevelByIdNotFound() {
        Long levelId = 1L;
        when(levelRepository.findById(levelId)).thenReturn(Optional.empty());

        Exception e = assertThrows(EntityNotFoundException.class, () -> secondRoundLevelService.getLevelById(levelId));

        assertEquals("Уровень с id 1 не найден", e.getMessage());
    }

    @Test
    void testDeleteLevel() throws EntityNotFoundException {
        Long levelId = 1L;
        when(levelRepository.existsById(levelId)).thenReturn(true);

        secondRoundLevelService.deleteLevel(levelId);

        verify(levelRepository).deleteById(levelId);
    }

    @Test
    void testGetRandomLevel() throws EntityNotFoundException, NoSuchRoundException, NoLevelsLeftException {
        int round = 2;
        Long levelId = 1L;
        SecondRoundLevel level = new SecondRoundLevel();

        when(levelsSessionCache.getRoundSize(round)).thenReturn(1);
        when(levelsSessionCache.getLevelId(round, 0)).thenReturn(levelId);
        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));

        SecondRoundLevel result = secondRoundLevelService.getRandomLevel(round);

        assertNotNull(result);
        verify(levelsSessionCache).removeLevel(round, 0);
    }

    @Test
    void testGetRandomLevelNoLevelsLeft() throws NoSuchRoundException {
        int round = 2;
        when(levelsSessionCache.getRoundSize(round)).thenReturn(0);

        Exception e = assertThrows(NoLevelsLeftException.class, () -> secondRoundLevelService.getRandomLevel(round));

        assertEquals("Уровней в раунде 2 больше нет", e.getMessage());
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

        SecondRoundLevel level = SecondRoundLevel.builder()
                .correctAnimal(correctAnimal)
                .build();

        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));
        when(animalService.getAnimalById(userAnswer)).thenReturn(correctAnimal);

        assertTrue(secondRoundLevelService.isCorrectAnswer(levelId, userAnswer));
    }

    @Test
    void testGetLevelCorrectAnimal() throws EntityNotFoundException {
        Long levelId = 1L;
        Animal correctAnimal = Animal.builder()
                .id(1L)
                .names(Map.of(Language.RUSSIAN, "Лев"))
                .descriptions(Map.of(Language.RUSSIAN, "Большая кошка"))
                .build();

        SecondRoundLevel level = SecondRoundLevel.builder()
                .correctAnimal(correctAnimal)
                .build();

        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));

        Animal result = secondRoundLevelService.getLevelCorrectAnimal(levelId);

        assertEquals(correctAnimal, result);
    }
}