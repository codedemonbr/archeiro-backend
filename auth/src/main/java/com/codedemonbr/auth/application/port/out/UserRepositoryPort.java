package com.codedemonbr.auth.application.port.out;

import com.codedemonbr.auth.domain.User;

import java.util.Optional;

public interface UserRepositoryPort {

    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    User save(User user);
    Optional<User> findById(Long id);
    // === NOVO MÉTODO ===
    Optional<User> findByEmail(String email);
}