package com.good.animalsgame.extern.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
@Tag(name = "SessionController", description = "Контроллер для управления HTTP-сессиями")
public class SessionController {

    @Operation(summary = "Завершить текущую HTTP-сессию", description = "Завершает сессию запросов")
    @ApiResponse(responseCode = "200", description = "HTTP-сессия завершена")
    @PostMapping("/invalidate")
    public ResponseEntity<Void> invalidateSession(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
