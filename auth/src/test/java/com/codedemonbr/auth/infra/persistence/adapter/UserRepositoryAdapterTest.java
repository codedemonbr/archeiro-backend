package com.codedemonbr.auth.infra.persistence.adapter;

import com.codedemonbr.auth.domain.User;
import com.codedemonbr.auth.infra.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepositoryAdapter - Testes Unitários de Delegação")
class UserRepositoryAdapterTest {

    @Mock
    private UserRepository jpaRepository;

    @InjectMocks
    private UserRepositoryAdapter adapter;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .nome("Thiago Henrique")
                .cpf("52998224725")
                .telefone("(11) 99999-9999")
                .email("thiago@exemplo.com")
                .senha("$2a$10$hashed")
                .build();
    }

    @Test
    @DisplayName("existsByEmail() deve delegar para JpaRepository e retornar o resultado")
    void shouldDelegateExistsByEmailAndReturnResult() {
        // Given
        String email = "thiago@exemplo.com";
        when(jpaRepository.existsByEmail(eq(email))).thenReturn(true);

        // When
        boolean result = adapter.existsByEmail(email);

        // Then
        assertThat(result).isTrue();
        verify(jpaRepository).existsByEmail(eq(email));
        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    @DisplayName("existsByCpf() deve delegar para JpaRepository e retornar o resultado")
    void shouldDelegateExistsByCpfAndReturnResult() {
        // Given
        String cpf = "52998224725";
        when(jpaRepository.existsByCpf(eq(cpf))).thenReturn(false);

        // When
        boolean result = adapter.existsByCpf(cpf);

        // Then
        assertThat(result).isFalse();
        verify(jpaRepository).existsByCpf(eq(cpf));
        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    @DisplayName("save() deve delegar para JpaRepository e retornar o usuário salvo")
    void shouldDelegateSaveAndReturnSavedUser() {
        // Given
        when(jpaRepository.save(any(User.class))).thenReturn(sampleUser);

        // When
        User savedUser = adapter.save(sampleUser);

        // Then
        assertThat(savedUser).isSameAs(sampleUser);
        verify(jpaRepository).save(eq(sampleUser));
        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    @DisplayName("save() deve chamar save com o usuário exato passado")
    void shouldCallSaveWithExactUserArgument() {
        // Given
        when(jpaRepository.save(any())).thenReturn(sampleUser);

        // When
        adapter.save(sampleUser);

        // Then
        verify(jpaRepository).save(eq(sampleUser));
    }
}