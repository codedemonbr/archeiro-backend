package com.codedemonbr.auth.application.usecase;

import com.codedemonbr.auth.application.port.out.UserRepositoryPort;
import com.codedemonbr.auth.domain.JwtService;
import com.codedemonbr.auth.domain.User;
import com.codedemonbr.auth.domain.exception.AuthenticationException;
import com.codedemonbr.auth.dto.LoginRequest;
import com.codedemonbr.auth.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginUseCase {

    private final UserRepositoryPort repositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse execute(LoginRequest request) {
        User user = repositoryPort.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Credenciais inválidas"));

        if (!user.isSenhaValida(request.getSenha(), passwordEncoder)) {
            throw new AuthenticationException("Credenciais inválidas");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(token, "Bearer");
    }
}