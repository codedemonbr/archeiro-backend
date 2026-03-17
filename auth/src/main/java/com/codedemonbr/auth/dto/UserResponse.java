package com.codedemonbr.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    // Nunca incluímos a senha na resposta!
}