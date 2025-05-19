package com.good.animalsgame.app.service;

import com.good.animalsgame.app.cache.LevelsSessionCache;
import com.good.animalsgame.app.repository.SecondRoundLevelRepository;
import com.good.animalsgame.domain.SecondRoundLevel;
import com.good.animalsgame.exception.EntityNotFoundException;
import com.good.animalsgame.exception.IncorrectLevelException;
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

        boolean isAnswerCorrect = level.getCorrectAnimal() == level.getAnimalInQuestion();
        boolean isAnimalsEmpty = level.getAnimals().isEmpty();

        if (isAnswerCorrect && !isAnimalsEmpty || !isAnswerCorrect && isAnimalsEmpty) {
            throw new IncorrectLevelException(
                    isAnswerCorrect
                            ? "Список животных должен быть пустым при совпадении верного ответа и вопроса"
                            : "Список животных не должен быть пустым при НЕ совпадении верного ответа и вопроса"
            );
        }

        return super.createLevel(level);
    }

    /**
     * Проверяет корректность ответа пользователя на вопрос "да/нет" во 2 раунде игры
     *
     * @param levelId идентификатор уровня
     * @param userAnswer ответ пользователя
     */
    public boolean isCorrectYesNoAnswer(Long levelId, boolean userAnswer) throws EntityNotFoundException {
        SecondRoundLevel level = getLevelById(levelId);
        boolean rightAnswerForLevel = (level.getCorrectAnimal() == level.getAnimalInQuestion());
        return userAnswer == rightAnswerForLevel;
    }
}
