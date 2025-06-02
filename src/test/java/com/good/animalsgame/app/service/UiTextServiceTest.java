package com.good.animalsgame.app.service;

import com.good.animalsgame.app.repository.UiTextRepository;
import com.good.animalsgame.domain.Language;
import com.good.animalsgame.domain.UiText;
import com.good.animalsgame.exception.EntityDuplicateException;
import com.good.animalsgame.exception.EntityNotFoundException;
import com.good.animalsgame.exception.LanguageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UiTextServiceTest {

    @Mock
    private UiTextRepository uiTextRepository;

    @InjectMocks
    private UiTextService uiTextService;

    @Test
    void testCreateUiText() throws EntityDuplicateException {
        String title = "title";
        when(uiTextRepository.existsByTitle(title)).thenReturn(false);

        UiText uiText = UiText.builder()
                .id(1L)
                .title(title)
                .texts(new HashMap<>())
                .build();

        uiTextService.createUiText(uiText);

        verify(uiTextRepository).save(uiText);
    }

    @Test
    void testCreateUiTextDuplicate() {
        String title = "title";
        when(uiTextRepository.existsByTitle(title)).thenReturn(true);

        UiText uiText = UiText.builder()
                .id(1L)
                .title(title)
                .texts(new HashMap<>())
                .build();

        Exception e = assertThrows(EntityDuplicateException.class, () -> uiTextService.createUiText(uiText));

        assertEquals("UI-текст с названием title уже существует!", e.getMessage());

        verify(uiTextRepository, never()).save(uiText);
    }

    @Test
    void testGetUiTextById() throws EntityNotFoundException {
        Long id = 1L;
        String title = "title";

        UiText uiText = UiText.builder()
                .id(id)
                .title(title)
                .texts(new HashMap<>())
                .build();

        when(uiTextRepository.findById(id)).thenReturn(Optional.of(uiText));

        UiText foundUiText = uiTextService.getUiTextById(id);

        assertEquals("title", foundUiText.getTitle());
    }

    @Test
    void testGetUiTextByTitle() throws EntityNotFoundException {
        Long id = 1L;
        String title = "title";

        UiText uiText = UiText.builder()
                .id(id)
                .title(title)
                .texts(new HashMap<>())
                .build();

        when(uiTextRepository.findByTitle(title)).thenReturn(Optional.of(uiText));

        UiText foundUiText = uiTextService.getUiTextByTitle(title);

        assertEquals(1L, foundUiText.getId());
    }

    @Test
    void testGetUiTextByTitleNotFound() {
        String title = "title";
        when(uiTextRepository.findByTitle(title)).thenReturn(Optional.empty());

        Exception e = assertThrows(EntityNotFoundException.class, () -> uiTextService.getUiTextByTitle(title));
        assertEquals("Не найден UI-текст с названием title", e.getMessage());
    }

    @Test
    void testGetUiTextByIdNotFound() {
        Long id = 1L;
        when(uiTextRepository.findById(id)).thenReturn(Optional.empty());

        Exception e = assertThrows(EntityNotFoundException.class, () -> uiTextService.getUiTextById(id));
        assertEquals("Не найден UI-текст с ID 1", e.getMessage());
    }

    @Test
    void testDeleteUiText() throws EntityNotFoundException {
        when(uiTextRepository.existsById(1L)).thenReturn(true);
        uiTextService.deleteUiText(1L);
        verify(uiTextRepository).deleteById(1L);
    }

    @Test
    void testGetAllUiTexts() {
        UiText uiText1 = UiText.builder()
                .id(1L)
                .title("title1")
                .texts(new HashMap<>())
                .build();
        UiText uiText2 = UiText.builder()
                .id(2L)
                .title("title2")
                .texts(new HashMap<>())
                .build();

        when(uiTextRepository.findAll()).thenReturn(List.of(uiText1, uiText2));

        List<UiText> foundTexts = uiTextService.getAllUiTexts();

        assertEquals(2, foundTexts.size());
        assertTrue(foundTexts.contains(uiText1));
        assertTrue(foundTexts.contains(uiText2));
    }

    @Test
    void testAddLanguage() throws LanguageException, EntityNotFoundException {
        UiText uiText = UiText.builder()
                .id(1L)
                .title("title")
                .texts(new HashMap<>(Map.of(Language.RUSSIAN, "r_text")))
                .build();

        when(uiTextRepository.findById(1L)).thenReturn(Optional.of(uiText));

        uiTextService.addLanguage(1L, "ENGLISH", "e_text");
        assertEquals("e_text", uiText.getTexts().get(Language.ENGLISH));
        verify(uiTextRepository).save(uiText);
    }

    @Test
    void testAddLanguageInvalid() {
        UiText uiText = UiText.builder()
                .id(1L)
                .title("title")
                .texts(new HashMap<>(Map.of(Language.RUSSIAN, "r_text")))
                .build();

        when(uiTextRepository.findById(1L)).thenReturn(Optional.of(uiText));

        Exception e = assertThrows(LanguageException.class, () -> uiTextService.addLanguage(1L, "INVALID", "e_text"));

        assertEquals("Нет языка INVALID", e.getMessage());
    }

    @Test
    void testAddLanguageAlreadyExists() {
        UiText uiText = UiText.builder()
                .id(1L)
                .title("title")
                .texts(new HashMap<>(Map.of(Language.RUSSIAN, "r_text")))
                .build();

        when(uiTextRepository.findById(1L)).thenReturn(Optional.of(uiText));

        Exception e = assertThrows(LanguageException.class, () -> uiTextService.addLanguage(1L, "RUSSIAN", "r2_text"));

        assertEquals("Язык RUSSIAN уже есть!", e.getMessage());
    }

    @Test
    void testRemoveLanguage() throws EntityNotFoundException, LanguageException {
        UiText uiText = UiText.builder()
                .id(1L)
                .title("title")
                .texts(new HashMap<>(Map.of(Language.RUSSIAN, "r_text", Language.ENGLISH, "e_text")))
                .build();

        when(uiTextRepository.findById(1L)).thenReturn(Optional.of(uiText));

        uiTextService.removeLanguage(1L, "ENGLISH");

        assertFalse(uiText.getTexts().containsKey(Language.ENGLISH));
        verify(uiTextRepository).save(uiText);
    }

    @Test
    void testRemoveLanguageInvalid() {
        UiText uiText = UiText.builder()
                .id(1L)
                .title("title")
                .texts(new HashMap<>(Map.of(Language.RUSSIAN, "r_text")))
                .build();

        when(uiTextRepository.findById(1L)).thenReturn(Optional.of(uiText));

        Exception e = assertThrows(LanguageException.class, () -> uiTextService.removeLanguage(1L, "INVALID"));

        assertEquals("Нет языка INVALID", e.getMessage());
    }

    @Test
    void testRemoveLanguageNotExists() {
        UiText uiText = UiText.builder()
                .id(1L)
                .title("title")
                .texts(new HashMap<>(Map.of(Language.RUSSIAN, "r_text")))
                .build();

        when(uiTextRepository.findById(1L)).thenReturn(Optional.of(uiText));

        Exception e = assertThrows(LanguageException.class, () -> uiTextService.removeLanguage(1L, "ENGLISH"));

        assertEquals("Язык ENGLISH отсутствует у текста!", e.getMessage());
    }

    @Test
    void testRemoveLanguageLast() {
        UiText uiText = UiText.builder()
                .id(1L)
                .title("title")
                .texts(new HashMap<>(Map.of(Language.RUSSIAN, "r_text")))
                .build();

        when(uiTextRepository.findById(1L)).thenReturn(Optional.of(uiText));

        Exception e = assertThrows(LanguageException.class, () -> uiTextService.removeLanguage(1L, "RUSSIAN"));

        assertEquals("Невозможно удалить последний язык!", e.getMessage());
    }
}