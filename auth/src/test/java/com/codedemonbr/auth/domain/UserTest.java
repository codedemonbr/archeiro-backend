package com.codedemonbr.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Entity - Testes unitários leves")
class UserTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("isSenhaValida() → senha correta deve retornar true")
    void shouldReturnTrueWhenPasswordMatches() {
        // Given
        User user = User.builder()
                .nome("Thiago Henrique")
                .cpf("52998224725")
                .telefone("(11) 99999-9999")
                .email("thiago@exemplo.com")
                .senha("$2a$10$hashedPasswordHere") // senha já hasheada
                .build();

        String senhaDigitada = "senhaSegura123";

        when(passwordEncoder.matches(senhaDigitada, user.getSenha()))
                .thenReturn(true);

        // When & Then
        assertThat(user.isSenhaValida(senhaDigitada, passwordEncoder)).isTrue();
    }

    @Test
    @DisplayName("isSenhaValida() → senha incorreta deve retornar false")
    void shouldReturnFalseWhenPasswordDoesNotMatch() {
        // Given
        User user = User.builder()
                .nome("Thiago Henrique")
                .cpf("52998224725")
                .telefone("(11) 99999-9999")
                .email("thiago@exemplo.com")
                .senha("$2a$10$hashedPasswordHere")
                .build();

        String senhaDigitada = "senhaErrada123";

        when(passwordEncoder.matches(senhaDigitada, user.getSenha()))
                .thenReturn(false);

        // When & Then
        assertThat(user.isSenhaValida(senhaDigitada, passwordEncoder)).isFalse();
    }
}