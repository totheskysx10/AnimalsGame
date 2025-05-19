package com.good.animalsgame.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Текст в пользовательском интерфейсе
 */
@Entity
@Table(name = "ui_texts")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UiText {

    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    /**
     * Название текста
     */
    @Getter
    private String title;

    /**
     * Тексты, язык -> текст на языке
     */
    @Getter
    @ElementCollection
    @CollectionTable(
            name = "multilingual_ui_texts",
            joinColumns = @JoinColumn(name = "ui_text_id")
    )
    @MapKeyColumn(name = "language")
    private Map<Language, String> texts;
}
