package com.codedemonbr.auth.domain.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Usuário com ID " + id + " não encontrado.");
    }
}