package com.good.animalsgame.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Репозиторий уровней
 * @param <T> тип уровня (раунд)
 * @param <ID> тип идентификатора
 */
@NoRepositoryBean
public interface LevelRepository<T, ID> extends JpaRepository<T, ID> {

    /**
     * Возвращает список всех идентификаторов уровней
     */
    @Query("SELECT l.id FROM #{#entityName} l")
    List<Long> findLevelIds();
}
