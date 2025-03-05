package com.good.animalsgame.app.repository;

import com.good.animalsgame.domain.SecondRoundLevel;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий уровней второго раунда
 */
@Repository
public interface SecondRoundLevelRepository extends LevelRepository<SecondRoundLevel, Long> {
}
