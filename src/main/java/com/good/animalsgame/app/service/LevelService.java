package com.good.animalsgame.app.service;

import com.good.animalsgame.app.cache.LevelsSessionCache;
import com.good.animalsgame.domain.Level;
import com.good.animalsgame.exception.LevelNotFoundException;
import com.good.animalsgame.exception.NoSuchRoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Random;

/**
 * Абстрактный сервис для работы с уровнями
 * @param <T> тип уровня (раунд)
 * @param <R> тип репозитория
 */
public abstract class LevelService<T extends Level, R extends JpaRepository<T, Long>> {

    private final LevelsSessionCache levelsSessionCache;
    private final R levelRepository;
    Random random = new Random();
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected LevelService(LevelsSessionCache levelsSessionCache,
                           R levelRepository) {
        this.levelsSessionCache = levelsSessionCache;
        this.levelRepository = levelRepository;
    }

    /**
     * Создаёт уровень
     *
     * @param level уровень
     * @return созданный уровень
     */
    public T createLevel(T level) {
        if (level == null) {
            throw new IllegalArgumentException("Уровень не может быть null");
        }

        try {
            T savedLevel = levelRepository.save(level);
            log.info("Создан новый уровень с id {}", savedLevel.getId());
            return savedLevel;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании уровня", e);
        }
    }

    /**
     * Получает уровень по идентификатору.
     *
     * @param levelId идентификатор
     * @throws LevelNotFoundException если уровень не найден
     */
    public T getLevelById(long levelId) throws LevelNotFoundException {
        T foundLevel = levelRepository.findById(levelId).orElse(null);
        if (foundLevel == null) {
            throw new LevelNotFoundException(String.format("Уровень с id %d не найден", levelId));
        } else {
            log.debug("Найден уровень с id {}", levelId);
            return foundLevel;
        }
    }

    /**
     * Удаляет уровень по идентификатору.
     *
     * @param levelId идентификатор
     */
    public void deleteLevel(long levelId) throws LevelNotFoundException {
        T level = getLevelById(levelId);
        levelRepository.deleteById(levelId);
        log.info("Удален уровень с id {}", levelId);
    }

    /**
     * Возвращает случайный уровень, удаляя его из кэша.
     * Удаляет, чтобы пользователь не ловил дублирования
     *
     * @param round раунд
     * @throws LevelNotFoundException если не найден уровень
     */
    public T getRandomLevel(int round) throws LevelNotFoundException, NoSuchRoundException {
        int randomLevelIndex = random.nextInt(levelsSessionCache.getRoundSize(round));

        try {
            Long randomLevelId = levelsSessionCache.getLevel(round, randomLevelIndex);
            T randomLevel = levelRepository.findById(randomLevelId).orElse(null);
            if (randomLevel == null) {
                throw new LevelNotFoundException(String.format("Уровень с id %d не найден", randomLevelId));
            } else {
                levelsSessionCache.removeLevel(round, randomLevelIndex);
                return randomLevel;
            }
        } catch (IndexOutOfBoundsException e) {
            throw new LevelNotFoundException(String.format("Не найден уровень с индексом %d", randomLevelIndex));
        }
    }
}
