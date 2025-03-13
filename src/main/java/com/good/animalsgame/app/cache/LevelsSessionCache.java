package com.good.animalsgame.app.cache;

import com.good.animalsgame.app.repository.LevelRepository;
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

    /**
     * Номер раунда в виде строки (из имени бина) -> репозиторий
     */
    private final Map<String, LevelRepository<?, ?>> levelRepositories;

    public LevelsSessionCache(Map<String, LevelRepository<?, ?>> levelRepositories) {
        this.levelRepositories = levelRepositories;
    }

    /**
     * Инициализация кэша - при начале новой сессии для неё подгружается список всех айди уровней всех раундов
     */
    @PostConstruct
    void init() {
        roundsCache.clear();
        levelRepositories.forEach((roundKey, levelRepository) -> {
            List<Long> levelIds = levelRepository.findLevelIds();
            Integer roundNumber = Integer.parseInt(roundKey);
            roundsCache.put(roundNumber, levelIds);
        });
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
