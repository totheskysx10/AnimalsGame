package com.good.animalsgame.app.repository;

import com.good.animalsgame.domain.UiText;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий текстов интерфейса
 */
public interface UiTextRepository extends JpaRepository<UiText, Long> {

    /**
     * Ищет все тексты
     */
    List<UiText> findAll();

    /**
     * Ищет тексты по названию
     * @param title название
     */
    Optional<UiText> findByTitle(String title);

    /**
     * Удаляет тексты по названию
     * @param title название
     */
    void deleteByTitle(String title);

    /**
     * Проверяет существование текста по названию
     * @param title название
     */
    boolean existsByTitle(String title);
}
