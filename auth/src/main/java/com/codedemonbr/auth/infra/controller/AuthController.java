package com.codedemonbr.auth.infra.controller;

import com.codedemonbr.auth.application.usecase.CreateUserUseCase;
import com.codedemonbr.auth.application.usecase.FindUserByIdUseCase;
import com.codedemonbr.auth.application.usecase.LoginUseCase;
import com.codedemonbr.auth.domain.exception.UserAlreadyExistsException;
import com.codedemonbr.auth.dto.CreateUserRequest;
import com.codedemonbr.auth.dto.LoginRequest;
import com.codedemonbr.auth.dto.LoginResponse;
import com.codedemonbr.auth.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@Tag(name = "Usuários", description = "Endpoints de cadastro e autenticação")
public class AuthController {

    private final CreateUserUseCase createUserUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;
    private final LoginUseCase loginUseCase;


    @PostMapping("/users")
    @Operation(
            summary = "Cadastrar novo usuário",
            description = "Cria um usuário com validação completa de CPF, email e telefone brasileiro."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (validação)"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado")
    })
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {

        UserResponse response = createUserUseCase.execute(request);

        // Retorna Location header (boa prática REST)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }


    @GetMapping("/users/{id}")
    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna os dados de um usuário pelo seu ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        UserResponse response = findUserByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Autenticar usuário",
            description = "Realiza login e retorna token JWT válido"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = loginUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    // No construtor:
    public AuthController(
            CreateUserUseCase createUserUseCase, 
            FindUserByIdUseCase findUserByIdUseCase,
            LoginUseCase loginUseCase
    ) {
        this.createUserUseCase = createUserUseCase;
        this.findUserByIdUseCase = findUserByIdUseCase;
        this.loginUseCase = loginUseCase;

    }

    // Tratamento específico de exceção de negócio (melhor UX)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // Classe interna simples para erro (pode virar DTO global depois)
    record ErrorResponse(int status, String message) {}
}