package com.good.animalsgame.extern.api.controller;

import com.good.animalsgame.app.service.UiTextService;
import com.good.animalsgame.domain.UiText;
import com.good.animalsgame.exception.EntityDuplicateException;
import com.good.animalsgame.exception.EntityNotFoundException;
import com.good.animalsgame.exception.LanguageException;
import com.good.animalsgame.extern.api.assembler.UiTextAssembler;
import com.good.animalsgame.extern.api.dto.AddUiTextLanguageDTO;
import com.good.animalsgame.extern.api.dto.UiTextDTO;
import com.good.animalsgame.extern.api.dto.ErrorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ui-texts")
@Tag(name = "UiTextController", description = "Контроллер для управления текстами интерфейса")
public class UiTextController {

    private final UiTextService uiTextService;
    private final UiTextAssembler uiTextAssembler;

    public UiTextController(UiTextService uiTextService, UiTextAssembler uiTextAssembler) {
        this.uiTextService = uiTextService;
        this.uiTextAssembler = uiTextAssembler;
    }

    @Operation(summary = "Создать UI-текст", description = "Создает новый тUI-текст")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "UI-текст успешно создан"),
            @ApiResponse(responseCode = "409", description = "UI-текст с таким названием уже существует"),
            @ApiResponse(responseCode = "404", description = "Ошибка языка")
    })
    @Transactional
    @PostMapping
    public ResponseEntity<Object> createUiText(@RequestBody @Valid UiTextDTO uiTextDTO) {
        try {
            UiText uiText = uiTextAssembler.toEntity(uiTextDTO);
            uiTextService.createUiText(uiText);
            return new ResponseEntity<>(uiTextAssembler.toModel(uiText), HttpStatus.CREATED);
        } catch (EntityDuplicateException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.CONFLICT);
        } catch (LanguageException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Получить UI-текст по ID", description = "Возвращает UI-текст по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "UI-текст успешно найден"),
            @ApiResponse(responseCode = "404", description = "UI-текст не найден")
    })
    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<UiTextDTO> getUiTextById(@PathVariable Long id) {
        try {
            UiText uiText = uiTextService.getUiTextById(id);
            return ResponseEntity.ok(uiTextAssembler.toModel(uiText));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получить UI-текст по названию", description = "Возвращает UI-текст по его названию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "UI-текст успешно найден"),
            @ApiResponse(responseCode = "404", description = "UI-текст не найден")
    })
    @Transactional
    @GetMapping("/title/{title}")
    public ResponseEntity<UiTextDTO> getUiTextByTitle(@PathVariable String title) {
        try {
            UiText uiText = uiTextService.getUiTextByTitle(title);
            return ResponseEntity.ok(uiTextAssembler.toModel(uiText));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Удалить UI-текст", description = "Удаляет UI-текст по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "UI-текст успешно удален"),
            @ApiResponse(responseCode = "404", description = "UI-текст не найден")
    })
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUiText(@PathVariable Long id) {
        try {
            uiTextService.deleteUiText(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получить все UI-тексты", description = "Возвращает все UI-тексты")
    @ApiResponse(responseCode = "200", description = "Список UI-текстов успешно получен")
    @Transactional
    @GetMapping
    public ResponseEntity<List<UiTextDTO>> getAllUiTexts() {
        List<UiText> uiTexts = uiTextService.getAllUiTexts();
        List<UiTextDTO> uiTextDtos = uiTexts.stream()
                .map(uiTextAssembler::toModel)
                .toList();
        return ResponseEntity.ok(uiTextDtos);
    }

    @Operation(summary = "Добавить язык к UI-тексту", description = "Добавляет язык к UI-тексту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Язык успешно добавлен"),
            @ApiResponse(responseCode = "404", description = "UI-текст не найден"),
            @ApiResponse(responseCode = "409", description = "Язык уже был добавлен или не найден")
    })
    @Transactional
    @PutMapping("/language/add/{id}")
    public ResponseEntity<Object> addLanguage(@PathVariable Long id, @RequestParam String language,
                                              @RequestBody @Valid AddUiTextLanguageDTO addUiTextLanguageDTO) {
        try {
            uiTextService.addLanguage(id, language, addUiTextLanguageDTO.getText());
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (LanguageException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO(e.getMessage()));
        }
    }

    @Operation(summary = "Удалить язык у UI-текста", description = "Удаляет язык у UI-текста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Язык успешно удален"),
            @ApiResponse(responseCode = "404", description = "UI-текст не найден"),
            @ApiResponse(responseCode = "409", description = "Язык не найден или это последний язык")
    })
    @Transactional
    @PutMapping("/language/remove/{id}")
    public ResponseEntity<Object> removeLanguage(@PathVariable Long id, @RequestParam String language) {
        try {
            uiTextService.removeLanguage(id, language);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (LanguageException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO(e.getMessage()));
        }
    }
}