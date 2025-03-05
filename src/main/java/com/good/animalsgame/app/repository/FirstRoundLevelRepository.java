package com.good.animalsgame.app.repository;

import com.good.animalsgame.domain.FirstRoundLevel;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий уровней первого раунда
 */
@Repository
public interface FirstRoundLevelRepository extends LevelRepository<FirstRoundLevel, Long> {
}
