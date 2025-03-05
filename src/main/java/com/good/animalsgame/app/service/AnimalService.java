package com.good.animalsgame.app.service;

import com.good.animalsgame.app.repository.AnimalRepository;
import com.good.animalsgame.domain.Animal;
import com.good.animalsgame.exception.AnimalDuplicateException;
import com.good.animalsgame.exception.AnimalNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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
     * @param animalName название животного
     * @return созданное животное
     * @throws AnimalDuplicateException если такое животное уже есть
     */
    public Animal createAnimal(String animalName) throws AnimalDuplicateException {
        String formattedName = formatAnimalName(animalName);
        if (animalRepository.existsByName(formattedName)) {
            throw new AnimalDuplicateException("Название животного должно быть уникальным!");
        }

        Animal animal = new Animal(formattedName, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        try {
            animalRepository.save(animal);
            log.info("Создано животное: {}", animalName);
            return animal;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании животного", e);
        }
    }

    /**
     * Ищет животного по названию
     *
     * @param name название
     */
    public Animal getAnimalByName(String name) throws AnimalNotFoundException {
        Animal foundAnimal = animalRepository.findByName(formatAnimalName(name));
        if (foundAnimal == null) {
            throw new AnimalNotFoundException(String.format("Животное с названием %s не найдено", name));
        } else {
            log.debug("Найдено животное {}", name);
            return foundAnimal;
        }
    }

    /**
     * Удаляет животное по названию
     *
     * @param name название
     */
    public void deleteAnimalByName(String name) throws AnimalNotFoundException {
        Animal foundAnimal = getAnimalByName(formatAnimalName(name));
        animalRepository.delete(foundAnimal);
        log.info("Удалено животное: {}", name);
    }

    /**
     * Форматирует название животного в формат "Как в предложениях"
     *
     * @param animalName название животного
     */
    private String formatAnimalName(String animalName) {
        return animalName.substring(0, 1).toUpperCase() + animalName.substring(1).toLowerCase();
    }
}
