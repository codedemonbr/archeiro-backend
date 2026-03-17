package com.codedemonbr.auth.infra.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(HelloController.class)   // Só carrega esse controller (rápido!)
@DisplayName("Tests for HelloController")
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /hello - Deve retornar mensagem de boas-vindas com status 200")
    void shouldReturnHelloMessageWithOkStatus() throws Exception {

        mockMvc.perform(get("/hello")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Olá! Auth Service está rodando com Spring Boot 4 + estrutura hexagonal! 🚀")))
                .andExpect(jsonPath("$.status", is("OK")));
    }

    @Test
    @DisplayName("GET /hello - Deve retornar JSON com estrutura correta")
    void shouldReturnValidJsonStructure() throws Exception {

        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").isString());
    }
}