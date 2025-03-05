package com.good.animalsgame.extern.api.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
public class SessionController {

    @PostMapping("/invalidate")
    public ResponseEntity<Void> invalidateSession(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
