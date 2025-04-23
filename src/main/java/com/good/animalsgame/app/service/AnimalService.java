package com.good.animalsgame.app.service;

import com.good.animalsgame.app.repository.AnimalRepository;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.domain.Language;
import com.good.animalsgame.exception.AnimalDuplicateException;
import com.good.animalsgame.exception.AnimalNotFoundException;
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
     * @throws AnimalDuplicateException если такое животное уже есть
     */
    public Animal createAnimal(Animal animal) throws AnimalDuplicateException {
        if (isDuplicateAnimal(animal.getName())) {
            throw new AnimalDuplicateException("Название животного должно быть уникальным! Даже если хоть по одному из языков есть пересечение, будет ошибка.");
        }

        animalRepository.save(animal);
        log.info("Создано животное: {}", animal.getId());

        return animal;
    }

    /**
     * Ищет животного по идентификатору
     *
     * @param id идентификатор
     */
    public Animal getAnimalById(Long id) throws AnimalNotFoundException {
        Optional<Animal> foundAnimal = animalRepository.findById(id);
        return foundAnimal.orElseThrow(() -> new AnimalNotFoundException(String.format("Животное с id %s не найдено", id)));
    }

    /**
     * Удаляет животного по идентификатору
     *
     * @param id идентификатор
     */
    public void deleteAnimalById(Long id) throws AnimalNotFoundException {
        if (animalRepository.existsById(id)) {
            animalRepository.deleteById(id);
            log.info("Удалено животное: {}", id);
        } else {
            throw new AnimalNotFoundException(String.format("Животное с id %s не найдено", id));
        }
    }

    /**
     * Добавляет животному язык
     * @param id идентификатор
     * @param language язык
     * @param name название
     * @param description описание
     * @throws LanguageException если язык уже есть
     */
    public void addLanguage(Long id, String language, String name, String description) throws AnimalNotFoundException, LanguageException {
        Animal animal = getAnimalById(id);

        Language languageConst;

        try {
            languageConst = Language.valueOf(language);
        } catch (IllegalArgumentException e) {
            throw new LanguageException("Нет языка " + language);
        }

        if (animal.getName().containsKey(languageConst)) {
            throw new LanguageException("Язык " + language + " уже есть!");
        }

        animal.getName().put(languageConst, name);
        animal.getDescription().put(languageConst, description);

        animalRepository.save(animal);
        log.info("Добавлен язык {} к уровню {}", language, id);
    }

    /**
     * Убирает у животного язык
     * @param id идентификатор
     * @param language язык
     * @throws LanguageException если язык отсутствует
     */
    public void removeLanguage(Long id, String language) throws AnimalNotFoundException, LanguageException {
        Animal animal = getAnimalById(id);

        Language languageConst;

        try {
            languageConst = Language.valueOf(language);
        } catch (IllegalArgumentException e) {
            throw new LanguageException("Нет языка " + language);
        }

        if (!animal.getName().containsKey(languageConst)) {
            throw new LanguageException("Язык " + language + " отсутствует!");
        }

        animal.getName().remove(languageConst);
        animal.getDescription().remove(languageConst);

        animalRepository.save(animal);
        log.info("Удалён язык {} у уровня {}", language, id);
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
                .flatMap(animal -> animal.getName().values().stream())
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
