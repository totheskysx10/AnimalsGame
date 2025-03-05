package com.good.animalsgame.app.service;

import com.good.animalsgame.app.cache.LevelsSessionCache;
import com.good.animalsgame.app.repository.SecondRoundLevelRepository;
import com.good.animalsgame.domain.SecondRoundLevel;
import com.good.animalsgame.exception.IncorrectLevelException;
import com.good.animalsgame.exception.LevelNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Сервис уровней второго раунда
 */
@Service
public class SecondRoundLevelService extends LevelService<SecondRoundLevel, SecondRoundLevelRepository> {

    public SecondRoundLevelService(LevelsSessionCache levelsSessionCache,
                                   SecondRoundLevelRepository secondRoundLevelRepository,
                                   AnimalService animalService) {
        super(levelsSessionCache, secondRoundLevelRepository, animalService);
    }

    @Override
    public SecondRoundLevel createLevel(SecondRoundLevel level) throws IncorrectLevelException {
        if (level == null) {
            throw new IllegalArgumentException("Уровень не может быть null");
        }

        if (level.getCorrectAnimal() == level.getAnimalInQuestion()) {
            if (!level.getAnimals().isEmpty()) {
                throw new IncorrectLevelException("Список животных должен быть пустым при правильном ответе ДА");
            }
        }

        try {
            return super.createLevel(level);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании уровня второго раунда", e);
        }
    }

    /**
     * Проверяет корректность ответа пользователя на вопрос "да/нет" во 2 раунде игры
     *
     * @param levelId идентификатор уровня
     * @param userAnswer ответ пользователя
     */
    public boolean isCorrectSecondRoundAnswer(Long levelId, boolean userAnswer) throws LevelNotFoundException {
        SecondRoundLevel level = getLevelById(levelId);
        boolean rightAnswerForLevel = (level.getCorrectAnimal() == level.getAnimalInQuestion());
        return userAnswer == rightAnswerForLevel;
    }
}
