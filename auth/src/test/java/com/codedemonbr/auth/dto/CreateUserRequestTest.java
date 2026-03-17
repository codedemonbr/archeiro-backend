package com.codedemonbr.auth.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateUserRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("DTO válido deve passar em todas as validações")
    void shouldPassAllValidationsWhenDataIsCorrect() {
        CreateUserRequest request = new CreateUserRequest(
                "Thiago Henrique",
                "55215612005",
                "(11) 99999-9999",
                "thiago@exemplo.com",
                "senhaSegura123"
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("CPF inválido deve falhar")
    void shouldFailWhenCpfIsInvalid() {
        CreateUserRequest request = new CreateUserRequest(
                "João Silva", "11111111111", "(11) 99999-9999", "joao@email.com", "12345678"
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().contains("CPF"))).isTrue();
    }

    @Test
    @DisplayName("Email inválido deve falhar")
    void shouldFailWhenEmailIsInvalid() {
        CreateUserRequest request = new CreateUserRequest(
                "Maria", "55215612005", "(11) 99999-9999", "email-invalido", "12345678"
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }
}