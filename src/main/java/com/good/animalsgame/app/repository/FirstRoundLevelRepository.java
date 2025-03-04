package com.good.animalsgame.app.repository;

import com.good.animalsgame.domain.FirstRoundLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий уровней первого раунда
 */
@Repository
public interface FirstRoundLevelRepository extends JpaRepository<FirstRoundLevel, Long> {

    /**
     * Возвращает список всех идентификаторов уровней первого раунда
     */
    @Query("SELECT l.id FROM FirstRoundLevel l")
    List<Long> findLevelIds();

    /**
     * Находит уровень первого раунда по идентификатору
     * @param id идентификатор
     */
    FirstRoundLevel findById(long id);
}
