package com.codedemonbr.auth.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")                    // nome explícito da tabela
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)  // evita problemas com JPA
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // incremental no H2 (e PostgreSQL)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;                    // armazenamos SEM máscara (11 dígitos)

    @Column(nullable = false, length = 15)
    private String telefone;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;                  // SEMPRE hash BCrypt (nunca plain text)

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    // Método de conveniência (boa prática)
    public boolean isSenhaValida(String senhaDigitada, PasswordEncoder encoder) {
        return encoder.matches(senhaDigitada, this.senha);
    }
}