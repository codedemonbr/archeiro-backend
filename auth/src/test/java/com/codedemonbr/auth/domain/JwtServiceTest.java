package com.codedemonbr.auth.domain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Unit Tests")
class JwtServiceTest {

    private static final String SECRET = "minha-chave-secreta-muito-longa-e-segura-256-bits-ou-mais-2026";
    private static final long EXPIRATION_MS = 900_000L; // 15 minutos

    @Mock
    private JwtService jwtService; // usaremos reflection para injetar valores

    @InjectMocks
    private JwtService serviceUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        // Injetamos os valores @Value via reflection (melhor prática para testes puros sem @SpringBootTest)
        setField(serviceUnderTest, "secret", SECRET);
        setField(serviceUnderTest, "expiration", EXPIRATION_MS);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Nested
    @DisplayName("Geração de Token")
    class GenerateTokenTests {

        @Test
        @DisplayName("Deve gerar token válido com email correto e claims esperados")
        void shouldGenerateValidTokenWithEmail() {
            String email = "thiago@exemplo.com";
            String token = serviceUnderTest.generateToken(email);

            assertThat(token).isNotBlank().isNotEmpty();

            Claims claims = extractClaims(token);
            assertThat(claims.getSubject()).isEqualTo(email);
            assertThat(claims.get("email")).isEqualTo(email);
            assertThat(claims.getIssuedAt()).isBefore(new Date());
            assertThat(claims.getExpiration()).isAfter(new Date());
        }

        @Test
        @DisplayName("Deve gerar token com expiração correta")
        void shouldGenerateTokenWithCorrectExpiration() {
            String token = serviceUnderTest.generateToken("user@test.com");

            Claims claims = extractClaims(token);
            long diffMs = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();

            assertThat(diffMs).isEqualTo(EXPIRATION_MS);
        }


        @Test
        @DisplayName("Deve falhar se secret for inválido (curto)")
        void shouldThrowWhenSecretIsTooShort() throws Exception {
            setField(serviceUnderTest, "secret", "curto"); // 5 bytes = 40 bits

            assertThatThrownBy(() -> serviceUnderTest.generateToken("email@test.com"))
                    .isInstanceOf(io.jsonwebtoken.security.WeakKeyException.class)
                    .hasMessageContaining("is not secure enough")           // parte comum e estável
                    .hasMessageContaining("MUST have a size >= 256 bits")   // parte da spec
                    .hasMessageContaining("RFC 7518");                      // confirma que é a exceção certa
        }
    }

    @Nested
    @DisplayName("Validação de Token")
    class ValidationTests {

        @Test
        @DisplayName("Token válido → deve retornar true")
        void shouldReturnTrueForValidTokenAndMatchingEmail() {
            String email = "valid@user.com";
            String token = serviceUnderTest.generateToken(email);

            boolean valid = serviceUnderTest.isTokenValid(token, email);

            assertThat(valid).isTrue();
        }

        @Test
        @DisplayName("Email diferente → deve retornar false")
        void shouldReturnFalseWhenEmailDoesNotMatch() {
            String token = serviceUnderTest.generateToken("thiago@exemplo.com");

            boolean valid = serviceUnderTest.isTokenValid(token, "outro@email.com");

            assertThat(valid).isFalse();
        }


        @Test
        @DisplayName("Token expirado → deve retornar false")
        void shouldReturnFalseForExpiredToken() throws Exception {
            // Forçamos uma expiração curta (1 segundo)
            setField(serviceUnderTest, "expiration", 1000L);

            String token = serviceUnderTest.generateToken("expired@user.com");

            // Esperamos um pouquinho para garantir que o token expire
            Thread.sleep(1500);

            boolean valid = serviceUnderTest.isTokenValid(token, "expired@user.com");

            assertThat(valid).isFalse();
        }

        @ParameterizedTest(name = "{index} → {1}")
        @MethodSource("invalidTokenScenarios")
        @DisplayName("Deve retornar false para tokens inválidos")
        void shouldReturnFalseForInvalidTokens(String token, String email, String scenario) {
            boolean valid = serviceUnderTest.isTokenValid(token, email);
            assertThat(valid).isFalse();
        }

        static Stream<Arguments> invalidTokenScenarios() {
            return Stream.of(
                    Arguments.of(null, "user@test.com", "token nulo"),
                    Arguments.of("", "user@test.com", "token vazio"),
                    Arguments.of("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid", "user@test.com", "token malformado")
            );
        }
    }

    @Nested
    @DisplayName("Extração de Claims")
    class ExtractTests {

        @Test
        @DisplayName("Deve extrair username (subject) corretamente")
        void shouldExtractUsernameCorrectly() {
            String email = "extract@me.com";
            String token = serviceUnderTest.generateToken(email);

            String extracted = serviceUnderTest.extractUsername(token);

            assertThat(extracted).isEqualTo(email);
        }

        @Test
        @DisplayName("Token inválido na extração → deve lançar exceção")
        void shouldThrowOnInvalidTokenExtraction() {
            assertThatThrownBy(() -> serviceUnderTest.extractUsername("invalid-jwt"))
                    .isInstanceOf(io.jsonwebtoken.MalformedJwtException.class);
        }
    }

    // Helpers para extrair claims em testes
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Helper para setar campos estáticos se precisar (usado em parameterized)
    private static void setStaticField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
