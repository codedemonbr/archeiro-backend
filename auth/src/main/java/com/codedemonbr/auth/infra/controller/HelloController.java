package com.codedemonbr.auth.infra.controller;

import com.codedemonbr.auth.dto.HelloResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping
    public ResponseEntity<HelloResponse> helloWorld() {
        HelloResponse response = new HelloResponse(
                "Olá! Auth Service está rodando com Spring Boot 4 + estrutura hexagonal! \uD83D\uDE80",
                "OK"
        );

        return ResponseEntity.ok(response);
    }
}
