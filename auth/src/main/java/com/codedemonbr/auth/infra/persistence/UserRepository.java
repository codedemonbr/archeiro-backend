package com.codedemonbr.auth.infra.persistence;

import com.codedemonbr.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adapter de saída (Infrastructure Layer)
 * Responsável por todas as operações de persistência do User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca usuário por email (usado no login)
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca usuário por CPF (usado para validação de duplicidade no cadastro)
     */
    Optional<User> findByCpf(String cpf);

    /**
     * Verifica se já existe um usuário com esse email
     * (útil para validações no UseCase)
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se já existe um usuário com esse CPF
     */
    boolean existsByCpf(String cpf);
}