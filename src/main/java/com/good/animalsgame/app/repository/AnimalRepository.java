package com.good.animalsgame.app.repository;

import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Репозиторий животных
 */
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    /**
     * Ищет животного по идентификатору
     * @param id идентификатор
     */
    Optional<Animal> findById(Long id);

    /**
     * Удаляет животного по идентификатору
     * @param id идентификатор
     */
    void deleteById(Long id);

    /**
     * Проверяет, есть ли такое животное
     * @param id идентификатор
     */
    boolean existsById(Long id);

    /**
     * Ищет животного по названию
     * @param name название
     */
    @Query("SELECT a FROM Animal a JOIN a.names n WHERE n = :name")
    Optional<Animal> findByName(@Param("name") String name);

}
