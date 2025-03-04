package com.good.animalsgame.app.service;

import com.good.animalsgame.app.repository.FirstRoundLevelRepository;
import com.good.animalsgame.app.cache.LevelsSessionCache;
import com.good.animalsgame.domain.FirstRoundLevel;
import org.springframework.stereotype.Service;

/**
 * Сервис уровней первого раунда
 */
@Service
public class FirstRoundLevelService extends LevelService<FirstRoundLevel, FirstRoundLevelRepository> {

    public FirstRoundLevelService(LevelsSessionCache levelsSessionCache,
                                  FirstRoundLevelRepository firstRoundLevelRepository) {
        super(levelsSessionCache, firstRoundLevelRepository);
    }

}
