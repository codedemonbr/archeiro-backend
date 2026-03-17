package com.codedemonbr.auth.infrastructure.controller;

import com.codedemonbr.auth.domain.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jwt-test")
public class JwtTestController {

    private final JwtService jwtService;

    public JwtTestController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generateTestToken() {
        String token = jwtService.generateToken("thiago@exemplo.com");
        return ResponseEntity.ok("Token gerado:\n\n" + token);
    }
}
