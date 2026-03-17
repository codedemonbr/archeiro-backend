package com.codedemonbr.auth.domain.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String campo) {
        super("Já existe um usuário cadastrado com este " + campo + ".");
    }
}