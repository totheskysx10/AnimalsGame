package com.good.animalsgame.app.service;

import com.good.animalsgame.app.cache.LevelsSessionCache;
import com.good.animalsgame.app.repository.SecondRoundLevelRepository;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.SecondRoundLevel;
import com.good.animalsgame.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

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
        Animal animal = new Animal("Лев", "Описание", null, null, null);
        SecondRoundLevel level = SecondRoundLevel.builder()
                .animalInQuestion(animal)
                .correctAnimal(animal)
                .animals(Collections.emptyList())
                .build();

        when(levelRepository.save(level)).thenReturn(level);

        SecondRoundLevel result = secondRoundLevelService.createLevel(level);

        assertNotNull(result);
        verify(levelRepository).save(level);
    }

    @Test
    void testCreateLevelAnswerNotMatchQuestion() throws IncorrectLevelException {
        Animal questionAnimal = new Animal("Лев", "Описание", null, null, null);
        Animal correctAnimal = new Animal("Тигр", "Описание", null, null, null);
        SecondRoundLevel level = SecondRoundLevel.builder()
                .animalInQuestion(questionAnimal)
                .correctAnimal(correctAnimal)
                .animals(Collections.nCopies(4, correctAnimal))
                .build();

        when(levelRepository.save(level)).thenReturn(level);

        SecondRoundLevel result = secondRoundLevelService.createLevel(level);

        assertNotNull(result);
        verify(levelRepository).save(level);
    }

    @Test
    void testCreateLevelAnswerMatchAnimalsNotEmpty() {
        Animal animal = new Animal("Лев", "Описание", null, null, null);
        SecondRoundLevel level = SecondRoundLevel.builder()
                .animalInQuestion(animal)
                .correctAnimal(animal)
                .animals(Collections.nCopies(4, animal))
                .build();

        Exception e = assertThrows(IncorrectLevelException.class, () -> secondRoundLevelService.createLevel(level));

        assertEquals("Список животных должен быть пустым при совпадении верного ответа и вопроса", e.getMessage());
    }

    @Test
    void testCreateLevelAnswerNotMatchAnimalsEmpty() {
        Animal questionAnimal = new Animal("Лев", "Описание", null, null, null);
        Animal correctAnimal = new Animal("Тигр", "Описание", null, null, null);
        SecondRoundLevel level = SecondRoundLevel.builder()
                .animalInQuestion(questionAnimal)
                .correctAnimal(correctAnimal)
                .animals(Collections.emptyList())
                .build();

        Exception e = assertThrows(IncorrectLevelException.class, () -> secondRoundLevelService.createLevel(level));

        assertEquals("Список животных не должен быть пустым при НЕ совпадении верного ответа и вопроса", e.getMessage());
    }

    @Test
    void testIsCorrectYesNoAnswerMatchQuestion() throws LevelNotFoundException {
        Long levelId = 1L;
        Animal animal = new Animal("Лев", "Описание", null, null, null);
        SecondRoundLevel level = SecondRoundLevel.builder()
                .animalInQuestion(animal)
                .correctAnimal(animal)
                .build();

        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));

        assertTrue(secondRoundLevelService.isCorrectYesNoAnswer(levelId, true));
        assertFalse(secondRoundLevelService.isCorrectYesNoAnswer(levelId, false));
    }

    @Test
    void testIsCorrectYesNoAnswerNotMatchQuestion() throws LevelNotFoundException {
        Long levelId = 1L;
        Animal questionAnimal = new Animal("Лев", "Описание", null, null, null);
        Animal correctAnimal = new Animal("Тигр", "Описание", null, null, null);
        SecondRoundLevel level = SecondRoundLevel.builder()
                .animalInQuestion(questionAnimal)
                .correctAnimal(correctAnimal)
                .build();

        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));

        assertFalse(secondRoundLevelService.isCorrectYesNoAnswer(levelId, true));
        assertTrue(secondRoundLevelService.isCorrectYesNoAnswer(levelId, false));
    }

    @Test
    void testGetLevelById() throws LevelNotFoundException {
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

        Exception e = assertThrows(LevelNotFoundException.class, () -> secondRoundLevelService.getLevelById(levelId));

        assertEquals("Уровень с id 1 не найден", e.getMessage());
    }

    @Test
    void testDeleteLevel() throws LevelNotFoundException {
        Long levelId = 1L;
        when(levelRepository.existsById(levelId)).thenReturn(true);

        secondRoundLevelService.deleteLevel(levelId);

        verify(levelRepository).deleteById(levelId);
    }

    @Test
    void testGetRandomLevel() throws LevelNotFoundException, NoSuchRoundException, NoLevelsLeftException {
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
    void testIsCorrectAnswer() throws LevelNotFoundException, AnimalNotFoundException {
        Long levelId = 1L;
        String userAnswer = "Лев";
        Animal correctAnimal = new Animal("Лев", "Описание", null, null, null);
        SecondRoundLevel level = SecondRoundLevel.builder()
                .correctAnimal(correctAnimal)
                .build();

        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));
        when(animalService.getAnimalByName(userAnswer)).thenReturn(correctAnimal);

        assertTrue(secondRoundLevelService.isCorrectAnswer(levelId, userAnswer));
    }

    @Test
    void testGetLevelCorrectAnimal() throws LevelNotFoundException {
        Long levelId = 1L;
        Animal correctAnimal = new Animal("Лев", "Описание", null, null, null);
        SecondRoundLevel level = SecondRoundLevel.builder()
                .correctAnimal(correctAnimal)
                .build();

        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));

        Animal result = secondRoundLevelService.getLevelCorrectAnimal(levelId);

        assertEquals(correctAnimal, result);
    }
}