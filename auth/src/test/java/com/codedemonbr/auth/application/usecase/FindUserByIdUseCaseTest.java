package com.codedemonbr.auth.application.usecase;

import com.codedemonbr.auth.application.port.out.UserRepositoryPort;
import com.codedemonbr.auth.domain.User;
import com.codedemonbr.auth.domain.exception.UserNotFoundException;
import com.codedemonbr.auth.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindUserByIdUseCase - ETOPS-1 (Cobertura Máxima)")
class FindUserByIdUseCaseTest {

    @Mock
    private UserRepositoryPort repositoryPort;

    @InjectMocks
    private FindUserByIdUseCase useCase;

    private final User sampleUser = User.builder()
            .id(42L)
            .nome("Thiago Henrique")
            .cpf("52998224725")
            .telefone("(11) 99999-9999")
            .email("thiago@exemplo.com")
            .senha("$2a$12$hashed")
            .criadoEm(LocalDateTime.now())
            .atualizadoEm(LocalDateTime.now())
            .build();

    @Test
    @DisplayName("Cenário feliz: usuário encontrado → deve retornar UserResponse completo")
    void shouldReturnUserResponseWhenUserExists() {
        // Given
        when(repositoryPort.findById(42L)).thenReturn(Optional.of(sampleUser));

        // When
        UserResponse response = useCase.execute(42L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(42L);
        assertThat(response.getNome()).isEqualTo("Thiago Henrique");
        assertThat(response.getCpf()).isEqualTo("52998224725");
        assertThat(response.getEmail()).isEqualTo("thiago@exemplo.com");

        // Verificação de interação
        verify(repositoryPort).findById(42L);
        verifyNoMoreInteractions(repositoryPort);
    }

    @Test
    @DisplayName("Usuário não encontrado → deve lançar UserNotFoundException")
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        // Given
        when(repositoryPort.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> useCase.execute(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Usuário com ID 999 não encontrado.");

        verify(repositoryPort).findById(999L);
    }

    @Test
    @DisplayName("Deve chamar findById com o ID exato recebido")
    void shouldCallRepositoryWithExactId() {
        when(repositoryPort.findById(anyLong())).thenReturn(Optional.of(sampleUser));

        useCase.execute(123L);

        verify(repositoryPort).findById(123L);
    }

    @Test
    @DisplayName("ID nulo → deve lançar IllegalArgumentException (defesa explícita)")
    void shouldThrowIllegalArgumentExceptionWhenIdIsNull() {
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID do usuário não pode ser nulo");
    }
}