package com.good.animalsgame.app.service;

import com.good.animalsgame.app.repository.UiTextRepository;
import com.good.animalsgame.domain.Language;
import com.good.animalsgame.domain.UiText;
import com.good.animalsgame.exception.EntityDuplicateException;
import com.good.animalsgame.exception.EntityNotFoundException;
import com.good.animalsgame.exception.LanguageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с текстами интерфейса
 */
@Service
public class UiTextService {

    private final UiTextRepository uiTextRepository;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public UiTextService(UiTextRepository uiTextRepository) {
        this.uiTextRepository = uiTextRepository;
    }

    /**
     * Создаёт новый текст интерфейса, сохраняет в БД
     * @param uiText текст
     * @throws EntityDuplicateException если текст с таким названием уже существует
     */
    public void createUiText(UiText uiText) throws EntityDuplicateException {
        if (uiTextRepository.existsByTitle(uiText.getTitle())) {
            throw new EntityDuplicateException("UI-текст с названием " + uiText.getTitle() + " уже существует!");
        }

        uiTextRepository.save(uiText);
        log.info("Создан UI-текст: {}", uiText.getTitle());
    }

    /**
     * Получает текст интерфейса по идентификатору
     * @param id идентификатор
     * @throws EntityNotFoundException если текст не найден
     */
    public UiText getUiTextById(Long id) throws EntityNotFoundException {
        Optional<UiText> uiText = uiTextRepository.findById(id);
        return uiText.orElseThrow(() -> new EntityNotFoundException("Не найден UI-текст с ID " + id));
    }

    /**
     * Получает текст интерфейса по названию
     * @param title название
     * @throws EntityNotFoundException если текст не найден
     */
    public UiText getUiTextByTitle(String title) throws EntityNotFoundException {
        Optional<UiText> uiText = uiTextRepository.findByTitle(title);
        return uiText.orElseThrow(() -> new EntityNotFoundException("Не найден UI-текст с названием " + title));
    }

    /**
     * Удаляет текст интерфейса по идентификатору
     * @param id идентификатор
     * @throws EntityNotFoundException если текст не найден
     */
    public void deleteUiText(Long id) throws EntityNotFoundException {
        if (uiTextRepository.existsById(id)) {
            uiTextRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Не найден UI-текст с ID " + id);
        }
    }

    /**
     * Возвращает все тексты интерфейса
     */
    public List<UiText> getAllUiTexts() {
        return uiTextRepository.findAll();
    }

    /**
     * Добавляет UI-тексту язык
     * @param id идентификатор
     * @param language язык
     * @param text текст
     * @throws LanguageException если язык уже есть или если не найден
     */
    public void addLanguage(Long id, String language, String text) throws EntityNotFoundException, LanguageException {
        UiText uiText = getUiTextById(id);
        Language languageConst;

        try {
            languageConst = Language.valueOf(language);
        } catch (IllegalArgumentException e) {
            throw new LanguageException("Нет языка " + language);
        }

        if (uiText.getTexts().containsKey(languageConst)) {
            throw new LanguageException("Язык " + language + " уже есть!");
        }

        uiText.getTexts().put(languageConst, text);

        uiTextRepository.save(uiText);
        log.info("Добавлен язык {} к UI-тексту {}", language, id);
    }

    /**
     * Убирает у UI-текста язык
     * @param id идентификатор
     * @param language язык
     * @throws LanguageException если язык отсутствует или если не найден
     */
    public void removeLanguage(Long id, String language) throws EntityNotFoundException, LanguageException {
        UiText uiText = getUiTextById(id);
        Language languageConst;

        try {
            languageConst = Language.valueOf(language);
        } catch (IllegalArgumentException e) {
            throw new LanguageException("Нет языка " + language);
        }

        if (!uiText.getTexts().containsKey(languageConst)) {
            throw new LanguageException("Язык " + language + " отсутствует у текста!");
        }

        if (uiText.getTexts().size() == 1) {
            throw new LanguageException("Невозможно удалить последний язык!");
        }

        uiText.getTexts().remove(languageConst);

        uiTextRepository.save(uiText);
        log.info("Удалён язык {} у UI-текста {}", language, id);
    }
}
