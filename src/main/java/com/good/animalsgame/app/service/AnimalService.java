package com.good.animalsgame.app.service;

import com.good.animalsgame.app.repository.AnimalRepository;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.Language;
import com.good.animalsgame.exception.EntityDuplicateException;
import com.good.animalsgame.exception.EntityNotFoundException;
import com.good.animalsgame.exception.LanguageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для работы с животными
 */
@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    /**
     * Создаёт животное
     *
     * @param animal животное
     * @throws EntityDuplicateException если такое животное уже есть
     */
    public void createAnimal(Animal animal) throws EntityDuplicateException {
        if (isDuplicateAnimal(animal.getNames())) {
            throw new EntityDuplicateException("Название животного должно быть уникальным! Даже если хоть по одному из языков есть пересечение, будет ошибка.");
        }

        animalRepository.save(animal);
        log.info("Создано животное: {}", animal.getId());
    }

    /**
     * Ищет животного по идентификатору
     *
     * @param id идентификатор
     */
    public Animal getAnimalById(Long id) throws EntityNotFoundException {
        Optional<Animal> foundAnimal = animalRepository.findById(id);
        return foundAnimal.orElseThrow(() -> new EntityNotFoundException(String.format("Животное с id %s не найдено", id)));
    }

    /**
     * Удаляет животного по идентификатору
     *
     * @param id идентификатор
     */
    public void deleteAnimalById(Long id) throws EntityNotFoundException {
        if (animalRepository.existsById(id)) {
            animalRepository.deleteById(id);
            log.info("Удалено животное: {}", id);
        } else {
            throw new EntityNotFoundException(String.format("Животное с id %s не найдено", id));
        }
    }

    /**
     * Добавляет животному язык
     * @param id идентификатор
     * @param language язык
     * @param name название
     * @param description описание
     * @throws LanguageException если язык уже есть или если не найден
     */
    public void addLanguage(Long id, String language, String name, String description) throws EntityNotFoundException, LanguageException {
        Animal animal = getAnimalById(id);

        Language languageConst;

        try {
            languageConst = Language.valueOf(language);
        } catch (IllegalArgumentException e) {
            throw new LanguageException("Нет языка " + language);
        }

        if (animal.getNames().containsKey(languageConst)) {
            throw new LanguageException("Язык " + language + " уже есть!");
        }

        animal.getNames().put(languageConst, name);
        animal.getDescriptions().put(languageConst, description);

        animalRepository.save(animal);
        log.info("Добавлен язык {} к животному {}", language, id);
    }

    /**
     * Убирает у животного язык
     * @param id идентификатор
     * @param language язык
     * @throws LanguageException если язык отсутствует или если не найден
     */
    public void removeLanguage(Long id, String language) throws EntityNotFoundException, LanguageException {
        Animal animal = getAnimalById(id);

        Language languageConst;

        try {
            languageConst = Language.valueOf(language);
        } catch (IllegalArgumentException e) {
            throw new LanguageException("Нет языка " + language);
        }

        if (!animal.getNames().containsKey(languageConst)) {
            throw new LanguageException("Язык " + language + " отсутствует у животного!");
        }

        if (animal.getNames().size() == 1) {
            throw new LanguageException("Невозможно удалить последний язык!");
        }

        animal.getNames().remove(languageConst);
        animal.getDescriptions().remove(languageConst);

        animalRepository.save(animal);
        log.info("Удалён язык {} у животного {}", language, id);
    }

    /**
     * Форматирует название животного в формат "Как в предложениях"
     *
     * @param animalName название животного
     */
    private String formatAnimalName(String animalName) {
        if (!animalName.isEmpty()) {
            return animalName.substring(0, 1).toUpperCase() + animalName.substring(1).toLowerCase();
        } else {
            return animalName;
        }
    }

    /**
     * Проверяет, что такого животного нет в базе
     * @param names названия на языках
     */
    private boolean isDuplicateAnimal(Map<Language, String> names) {
        List<Animal> allAnimals = animalRepository.findAll();

        Set<String> existingNameSet = allAnimals.stream()
                .flatMap(animal -> animal.getNames().values().stream())
                .map(this::formatAnimalName)
                .collect(Collectors.toSet());

        Set<String> newNames = names.values().stream()
                .map(this::formatAnimalName)
                .collect(Collectors.toSet());

        boolean result = false;

        for (String newName : newNames) {
            if (existingNameSet.contains(newName)) {
                result = true;
                break;
            }
        }

        return result;
    }
}
