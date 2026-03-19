package com.codedemonbr.auth.application.usecase;

import com.codedemonbr.auth.application.port.out.UserRepositoryPort;
import com.codedemonbr.auth.domain.exception.UserNotFoundException;
import com.codedemonbr.auth.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindUserByIdUseCase {

    private final UserRepositoryPort repositoryPort;

    public UserResponse execute(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do usuário não pode ser nulo");
        }

        return repositoryPort.findById(id)
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getNome(),
                        user.getCpf(),
                        user.getTelefone(),
                        user.getEmail()
                ))
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}