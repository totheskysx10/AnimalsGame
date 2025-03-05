package com.good.animalsgame.app.cache;

import com.good.animalsgame.app.repository.FirstRoundLevelRepository;
import com.good.animalsgame.app.repository.SecondRoundLevelRepository;
import com.good.animalsgame.exception.NoSuchRoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Кэш уровней пользователей.
 * Нужен, чтобы отслеживать - какие уровни уже пройдены пользователем, а какие ещё можно ему выдать.
 */
@Component
@SessionScope
public class LevelsSessionCache {

    /**
     * Номер раунда -> идентификаторы уровней
     */
    private final Map<Integer, List<Long>> roundsCache = new HashMap<>();

    private final FirstRoundLevelRepository firstRoundLevelRepository;
    private final SecondRoundLevelRepository secondRoundLevelRepository;

    private static final Integer FIRST_ROUND_NUMBER = 1;
    private static final Integer SECOND_ROUND_NUMBER = 2;

    public LevelsSessionCache(FirstRoundLevelRepository firstRoundLevelRepository,
                              SecondRoundLevelRepository secondRoundLevelRepository) {
        this.firstRoundLevelRepository = firstRoundLevelRepository;
        this.secondRoundLevelRepository = secondRoundLevelRepository;
    }

    /**
     * Инициализация кэша - при начале новой сессии для неё подгружается список всех айди уровней
     */
    @PostConstruct
    void init() {
        roundsCache.put(FIRST_ROUND_NUMBER, firstRoundLevelRepository.findLevelIds());
        roundsCache.put(SECOND_ROUND_NUMBER, secondRoundLevelRepository.findLevelIds());
    }

    /**
     * Получение размера списка уровней
     *
     * @param round номер раунда
     * @throws NoSuchRoundException если раунд не найден
     */
    public int getRoundSize(int round) throws NoSuchRoundException {
        List<Long> levels = roundsCache.get(round);
        if (levels != null) {
            return levels.size();
        } else {
            throw new NoSuchRoundException(String.format("Не найден раунд %d!", round));
        }
    }

    /**
     * Получение уровня для указанного раунда по индексу
     *
     * @param round номер раунда
     * @param index индекс уровня
     * @throws NoSuchRoundException если раунд не найден
     */
    public Long getLevel(int round, int index) throws NoSuchRoundException {
        List<Long> levels = roundsCache.get(round);
        if (levels != null) {
            return levels.get(index);
        } else {
            throw new NoSuchRoundException(String.format("Не найден раунд %d!", round));
        }
    }

    /**
     * Удаление уровня для указанного раунда по индексу за O(1)
     *
     * @param round номер раунда
     * @param index индекс уровня
     * @throws NoSuchRoundException если раунд не найден
     */
    public void removeLevel(int round, int index) throws NoSuchRoundException {
        List<Long> levels = roundsCache.get(round);
        if (levels != null) {
            levels.set(index, levels.getLast());
            levels.removeLast();
        } else {
            throw new NoSuchRoundException(String.format("Не найден раунд %d!", round));
        }
    }
}
