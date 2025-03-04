package com.good.animalsgame.app.service;

import com.good.animalsgame.app.cache.LevelsSessionCache;
import com.good.animalsgame.app.repository.SecondRoundLevelRepository;
import com.good.animalsgame.domain.SecondRoundLevel;
import com.good.animalsgame.exception.LevelNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Сервис уровней второго раунда
 */
@Service
public class SecondRoundLevelService extends LevelService<SecondRoundLevel, SecondRoundLevelRepository> {

    public SecondRoundLevelService(LevelsSessionCache levelsSessionCache,
                                   SecondRoundLevelRepository secondRoundLevelRepository) {
        super(levelsSessionCache, secondRoundLevelRepository);
    }

    /**
     * Проверяет корректность ответа пользователя на вопрос "да/нет" во 2 раунде игры
     *
     * @param levelId идентификатор уровня
     * @param userAnswer ответ пользователя
     */
    public boolean isCorrectAnswer(Long levelId, boolean userAnswer) throws LevelNotFoundException {
        SecondRoundLevel level = getLevelById(levelId);
        boolean correctAnswer = (level.getCorrectAnimal() == level.getAnimalInQuestion());
        return userAnswer == correctAnswer;
    }
}
