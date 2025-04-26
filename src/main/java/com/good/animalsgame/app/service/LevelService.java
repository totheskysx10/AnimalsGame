package com.good.animalsgame.app.service;

import com.good.animalsgame.app.cache.LevelsSessionCache;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.Level;
import com.good.animalsgame.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Random;

/**
 * Абстрактный сервис для работы с уровнями
 *
 * @param <T> тип уровня (раунд)
 * @param <R> тип репозитория
 */
public abstract class LevelService<T extends Level, R extends JpaRepository<T, Long>> {

    private final LevelsSessionCache levelsSessionCache;
    private final R levelRepository;
    private final AnimalService animalService;
    private final Random random = new Random();
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Размер списка с животными
     */
    private static final int ANIMALS_LIST_SIZE = 4;

    protected LevelService(LevelsSessionCache levelsSessionCache,
                           R levelRepository,
                           AnimalService animalService) {
        this.levelsSessionCache = levelsSessionCache;
        this.levelRepository = levelRepository;
        this.animalService = animalService;
    }

    /**
     * Создаёт уровень
     *
     * @param level уровень
     * @return созданный уровень
     */
    public T createLevel(T level) throws IncorrectLevelException {
        if (level == null) {
            throw new IllegalArgumentException("Уровень не может быть null");
        }

        if (!level.getAnimals().isEmpty()) {
            if (!level.getAnimals().contains(level.getCorrectAnimal())) {
                throw new IncorrectLevelException("Верный ответ должен содержаться в списке!");
            }
            if (level.getAnimals().size() != ANIMALS_LIST_SIZE) {
                throw new IncorrectLevelException("Список животных должен содержать ровно 4 элемента.");
            }
        }

        T savedLevel = levelRepository.save(level);
        log.info("Создан новый уровень с id {}", savedLevel.getId());
        return savedLevel;
    }

    /**
     * Получает уровень по идентификатору.
     *
     * @param levelId идентификатор
     * @throws EntityNotFoundException если уровень не найден
     */
    public T getLevelById(long levelId) throws EntityNotFoundException {
        Optional<T> foundLevel = levelRepository.findById(levelId);
        return foundLevel.orElseThrow(() -> new EntityNotFoundException(String.format("Уровень с id %d не найден", levelId)));
    }

    /**
     * Удаляет уровень по идентификатору.
     *
     * @param levelId идентификатор
     */
    public void deleteLevel(long levelId) throws EntityNotFoundException {
        if (levelRepository.existsById(levelId)) {
            levelRepository.deleteById(levelId);
            log.info("Удален уровень с id {}", levelId);
        } else {
            throw new EntityNotFoundException(String.format("Уровень с id %d не найден", levelId));
        }
    }

    /**
     * Возвращает случайный уровень, удаляя его из кэша.
     * Удаляет, чтобы пользователь не ловил дублирования
     *
     * @param round раунд
     * @throws EntityNotFoundException если не найден уровень
     */
    public T getRandomLevel(int round) throws EntityNotFoundException, NoSuchRoundException, NoLevelsLeftException {
        int roundSize = levelsSessionCache.getRoundSize(round);

        if (roundSize == 0) {
            throw new NoLevelsLeftException(String.format("Уровней в раунде %d больше нет", round));
        }

        int randomLevelIndex = random.nextInt(roundSize);

        try {
            Long randomLevelId = levelsSessionCache.getLevelId(round, randomLevelIndex);
            T randomLevel = levelRepository
                    .findById(randomLevelId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Уровень с id %d не найден", randomLevelId)));

            levelsSessionCache.removeLevel(round, randomLevelIndex);
            return randomLevel;
        } catch (IndexOutOfBoundsException e) {
            throw new EntityNotFoundException(String.format("Не найден уровень с индексом %d", randomLevelIndex));
        }
    }

    /**
     * Проверяет корректность выбранного пользователем животного
     *
     * @param levelId    идентификатор уровня
     * @param userAnswer ответ пользователя
     */
    public boolean isCorrectAnswer(Long levelId, Long userAnswer) throws EntityNotFoundException, EntityNotFoundException {
        T level = getLevelById(levelId);
        Animal userAnimal = animalService.getAnimalById(userAnswer);

        return level.getCorrectAnimal().equals(userAnimal);
    }

    /**
     * Возвращает верное животное уровня
     * @param levelId идентификатор уровня
     */
    public Animal getLevelCorrectAnimal(Long levelId) throws EntityNotFoundException {
        T level = getLevelById(levelId);
        return level.getCorrectAnimal();
    }
}
