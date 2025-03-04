package com.good.animalsgame.app.repository;

import com.good.animalsgame.domain.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий животных
 */
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    /**
     * Ищет животного по названию
     * @param name название
     */
    Animal findByName(String name);

    /**
     * Ищет животного по идентификатору
     * @param id идентификатор
     */
    Animal findById(long id);
}
