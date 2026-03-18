package com.codedemonbr.auth.infra.persistence.adapter;

import com.codedemonbr.auth.application.port.out.UserRepositoryPort;
import com.codedemonbr.auth.domain.User;
import com.codedemonbr.auth.infra.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * ADAPTER DE SAÍDA (Infrastructure Layer)
 *
 * Responsabilidade:
 * - Implementar o UserRepositoryPort
 * - Traduzir chamadas do UseCase para o JpaRepository real
 * - Ser o único lugar que conhece detalhes do Spring Data JPA
 *
 * Princípios seguidos:
 * - Thin Adapter (quase sem lógica)
 * - Single Responsibility
 * - Dependency Inversion (UseCase depende de abstração)
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository jpaRepository;   // JpaRepository real (o que criamos antes)

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return jpaRepository.existsByCpf(cpf);
    }

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

    // Se no futuro precisar de mais métodos (findById, delete, etc.), basta adicionar aqui
}