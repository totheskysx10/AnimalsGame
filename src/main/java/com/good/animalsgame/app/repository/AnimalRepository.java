package com.good.animalsgame.app.repository;

import com.good.animalsgame.domain.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий животных
 */
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    /**
     * Ищет животного по названию
     * @param name название
     */
    Optional<Animal> findByName(String name);

    /**
     * Удаляет животного по названию
     * @param name название
     */
    void deleteByName(String name);

    /**
     * Проверяет, есть ли такое животное
     * @param name название
     */
    boolean existsByName(String name);
}
