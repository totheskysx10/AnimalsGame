package com.good.animalsgame.app.repository;

import com.good.animalsgame.domain.SecondRoundLevel;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий уровней второго раунда
 */
@Repository("2")
public interface SecondRoundLevelRepository extends LevelRepository<SecondRoundLevel, Long> {
}
