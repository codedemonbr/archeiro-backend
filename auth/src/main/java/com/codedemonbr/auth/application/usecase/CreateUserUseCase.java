package com.codedemonbr.auth.application.usecase;

import com.codedemonbr.auth.application.port.out.UserRepositoryPort;
import com.codedemonbr.auth.domain.User;
import com.codedemonbr.auth.domain.exception.UserAlreadyExistsException;
import com.codedemonbr.auth.dto.CreateUserRequest;
import com.codedemonbr.auth.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case responsável por cadastrar um novo usuário.
 *
 * Regras de negócio aplicadas:
 * - Não permitir email duplicado
 * - Não permitir CPF duplicado
 * - Senha sempre armazenada com hash BCrypt
 * - Transação garantida
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CreateUserUseCase {

    private final UserRepositoryPort repositoryPort;
    private final PasswordEncoder passwordEncoder;

    /**
     * Executa o cadastro de usuário.
     *
     * @param request dados de entrada validados pelo Controller
     * @return UserResponse com dados do usuário criado (sem senha)
     * @throws UserAlreadyExistsException se email ou CPF já existirem
     */
    public UserResponse execute(CreateUserRequest request) {

        // === Regras de negócio ===
        if (repositoryPort.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("email");
        }

        if (repositoryPort.existsByCpf(request.getCpf())) {
            throw new UserAlreadyExistsException("CPF");
        }

        // === Transformação para domínio ===
        String hashedPassword = passwordEncoder.encode(request.getSenha());

        User user = User.builder()
                .nome(request.getNome())
                .cpf(request.getCpf())
                .telefone(request.getTelefone())
                .email(request.getEmail())
                .senha(hashedPassword)
                .build();

        // === Persistência ===
        User savedUser = repositoryPort.save(user);

        // === Resposta limpa (nunca expomos senha ou entidade) ===
        return new UserResponse(
                savedUser.getId(),
                savedUser.getNome(),
                savedUser.getCpf(),
                savedUser.getTelefone(),
                savedUser.getEmail()
        );
    }
}