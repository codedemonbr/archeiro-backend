package com.codedemonbr.auth.application.usecase;

import com.codedemonbr.auth.application.port.out.UserRepositoryPort;
import com.codedemonbr.auth.domain.User;
import com.codedemonbr.auth.domain.exception.UserAlreadyExistsException;
import com.codedemonbr.auth.dto.CreateUserRequest;
import com.codedemonbr.auth.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserUseCase - Testes Unitários (100% cobertura)")
class CreateUserUseCaseTest {

    @Mock
    private UserRepositoryPort repositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CreateUserUseCase useCase;

    private final CreateUserRequest request = new CreateUserRequest(
            "Thiago Henrique",
            "52998224725",
            "(11) 99999-9999",
            "thiago@exemplo.com",
            "senhaSegura123"
    );

    @Test
    @DisplayName("Cenário feliz: deve cadastrar usuário e retornar UserResponse")
    void shouldCreateUserSuccessfully() {
        // Given
        when(repositoryPort.existsByEmail(request.getEmail())).thenReturn(false);
        when(repositoryPort.existsByCpf(request.getCpf())).thenReturn(false);
        when(passwordEncoder.encode(request.getSenha())).thenReturn("$2a$10$hashedPassword123");
        when(repositoryPort.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L); // simula ID gerado
            return saved;
        });

        // When
        UserResponse response = useCase.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("Thiago Henrique");
        assertThat(response.getEmail()).isEqualTo("thiago@exemplo.com");
        assertThat(response.getCpf()).isEqualTo("52998224725");

        // Verificações de interação
        verify(repositoryPort).existsByEmail(request.getEmail());
        verify(repositoryPort).existsByCpf(request.getCpf());
        verify(passwordEncoder).encode(request.getSenha());
        verify(repositoryPort).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(repositoryPort.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("email");

        verify(repositoryPort, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já existe")
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        when(repositoryPort.existsByEmail(request.getEmail())).thenReturn(false);
        when(repositoryPort.existsByCpf(request.getCpf())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("CPF");

        verify(repositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve chamar o encode da senha antes de salvar")
    void shouldEncodePasswordBeforeSaving() {
        when(repositoryPort.existsByEmail(any())).thenReturn(false);
        when(repositoryPort.existsByCpf(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("$2a$10$hash");
        when(repositoryPort.save(any())).thenReturn(new User());

        useCase.execute(request);

        verify(passwordEncoder).encode(request.getSenha());
    }
}