package com.good.animalsgame.app.repository;

import com.good.animalsgame.domain.SecondRoundLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий уровней второго раунда
 */
@Repository
public interface SecondRoundLevelRepository extends JpaRepository<SecondRoundLevel, Long> {

    /**
     * Возвращает список всех идентификаторов уровней второго раунда
     */
    @Query("SELECT l.id FROM SecondRoundLevel l")
    List<Long> findLevelIds();

    /**
     * Находит уровень второго раунда по идентификатору
     * @param id идентификатор
     */
    SecondRoundLevel findById(long id);
}
