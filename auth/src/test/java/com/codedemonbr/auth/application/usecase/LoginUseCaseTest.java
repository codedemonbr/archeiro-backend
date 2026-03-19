package com.codedemonbr.auth.application.usecase;
import com.codedemonbr.auth.application.port.out.UserRepositoryPort;
import com.codedemonbr.auth.domain.JwtService;
import com.codedemonbr.auth.domain.User;
import com.codedemonbr.auth.domain.exception.AuthenticationException;
import com.codedemonbr.auth.dto.LoginRequest;
import com.codedemonbr.auth.dto.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginUseCase - ETOPS-1 (Cobertura 100%)")
class LoginUseCaseTest {

    @Mock
    private UserRepositoryPort repositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private final LoginRequest request = new LoginRequest("thiago@exemplo.com", "senhaSegura123");

    @Test
    @DisplayName("Login bem-sucedido → deve retornar token JWT")
    void shouldLoginSuccessfullyAndReturnJwt() {
        User user = User.builder()
                .id(1L)
                .email("thiago@exemplo.com")
                .senha("$2a$12$hashedPassword")
                .build();

        when(repositoryPort.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getSenha(), user.getSenha())).thenReturn(true);
        when(jwtService.generateToken(user.getEmail())).thenReturn("jwt.token.fake.123");

        LoginResponse response = loginUseCase.execute(request);

        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getTipo()).isEqualTo("Bearer");

        verify(repositoryPort).findByEmail(request.getEmail());
        verify(passwordEncoder).matches(request.getSenha(), user.getSenha());
        verify(jwtService).generateToken(user.getEmail());
    }

    @Test
    @DisplayName("Email não encontrado → deve lançar AuthenticationException")
    void shouldThrowWhenUserNotFound() {
        when(repositoryPort.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Credenciais inválidas");
    }

    @Test
    @DisplayName("Senha incorreta → deve lançar AuthenticationException")
    void shouldThrowWhenPasswordIsWrong() {
        User user = User.builder().email("thiago@exemplo.com").senha("$2a$12$hashed").build();

        when(repositoryPort.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Credenciais inválidas");
    }
}
