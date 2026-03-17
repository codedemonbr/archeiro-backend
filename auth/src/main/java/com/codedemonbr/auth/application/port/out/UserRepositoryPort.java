package com.codedemonbr.auth.application.port.out;

import com.codedemonbr.auth.domain.User;

public interface UserRepositoryPort {

    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    User save(User user);
}