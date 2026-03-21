package com.codedemonbr.auth.infra.controller;

import com.codedemonbr.auth.application.usecase.CreateUserUseCase;
import com.codedemonbr.auth.application.usecase.FindUserByIdUseCase;
import com.codedemonbr.auth.application.usecase.LoginUseCase;
import com.codedemonbr.auth.domain.exception.AuthenticationException;
import com.codedemonbr.auth.domain.exception.UserAlreadyExistsException;
import com.codedemonbr.auth.domain.exception.UserNotFoundException;
import com.codedemonbr.auth.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@DisplayName("AuthController - ETOPS-1 (Testes profundos de API)")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @MockitoBean
    private FindUserByIdUseCase findUserByIdUseCase;

    @MockitoBean
    private LoginUseCase loginUseCase;

    // ==================== CADASTRO ====================
    @Test
    @DisplayName("POST /auth/users - Sucesso → 201 Created + Location header")
    void shouldCreateUserAndReturn201() throws Exception {
        UserResponse response = new UserResponse(10L, "Thiago", "52998224725", "(11)99999-9999", "thiago@test.com");

        when(createUserUseCase.execute(any(CreateUserRequest.class))).thenReturn(response);

        String json = """
                {
                    "nome": "Thiago",
                    "cpf": "52998224725",
                    "telefone": "(11)99999-9999",
                    "email": "thiago@test.com",
                    "senha": "12345678"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @DisplayName("POST /users - Email duplicado → 409 Conflict")
    void shouldReturn409WhenEmailExists() throws Exception {
        when(createUserUseCase.execute(any(CreateUserRequest.class)))
                .thenThrow(new UserAlreadyExistsException("email"));

        String json = """
            {
                "nome": "Teste Duplicado",
                "cpf": "49387451003",
                "telefone": "(11)99999-9999",
                "email": "dup@test.com",
                "senha": "senhaValida123"
            }
            """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe um usuário cadastrado com este email."));
    }

    @Test
    @DisplayName("POST /users - Senha curta → 400 Bad Request (validação)")
    void shouldReturn400WhenPasswordIsTooShort() throws Exception {
        String json = """
            {
                "nome": "Teste",
                "cpf": "06399973090",
                "telefone": "(11)99999-9999",
                "email": "teste@test.com",
                "senha": "123"
            }
            """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("senha"))
                .andExpect(jsonPath("$.errors[0].message").value("Senha deve ter no mínimo 8 caracteres"));
    }

    // ==================== BUSCA POR ID ====================
    @Test
    @DisplayName("GET /auth/users/{id} - Sucesso → 200")
    void shouldReturnUserWhenFound() throws Exception {
        UserResponse response = new UserResponse(42L, "João", "12345678901", "(11)88888-8888", "joao@test.com");

        when(findUserByIdUseCase.execute(42L)).thenReturn(response);

        mockMvc.perform(get("/users/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.nome").value("João"));
    }

    @Test
    @DisplayName("GET /auth/users/{id} - Não encontrado → 404")
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(findUserByIdUseCase.execute(999L))
                .thenThrow(new UserNotFoundException(999L));

        mockMvc.perform(get("/auth/users/999"))
                .andExpect(status().isNotFound());
    }

    // ==================== LOGIN ====================
    @Test
    @DisplayName("POST /auth/login - Sucesso → 200 com token")
    void shouldLoginSuccessfully() throws Exception {
        LoginResponse loginResponse = new LoginResponse("eyJhbGciOiJIUzI1Ni...", "Bearer");

        when(loginUseCase.execute(any(LoginRequest.class))).thenReturn(loginResponse);

        String json = """
                {
                    "email": "thiago@test.com",
                    "senha": "12345678"
                }
                """;

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    @DisplayName("POST /login - Credenciais inválidas → 401")
    void shouldReturn401OnInvalidCredentials() throws Exception {
        when(loginUseCase.execute(any()))
                .thenThrow(new AuthenticationException("Credenciais inválidas"));

        String json = "{ \"email\":\"wrong@test.com\", \"senha\":\"errada\" }";

        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isUnauthorized());
    }
}