package ru.ivanov.Bank.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Провайдер для работы с JWT токенами.
 * 
 * <p>Предоставляет методы для генерации, валидации и извлечения информации
 * из JWT токенов. Использует HMAC-SHA алгоритм для подписи токенов.</p>
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2024
 */
@Component
public class JwtTokenProvider {
    /**
     * Секретный ключ для подписи JWT токенов.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Время жизни JWT токена в миллисекундах.
     */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Получает ключ для подписи JWT токенов.
     * 
     * @return секретный ключ для подписи
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Генерирует JWT токен для аутентифицированного пользователя.
     * 
     * @param authentication объект аутентификации
     * @return сгенерированный JWT токен
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Извлекает имя пользователя из JWT токена.
     * 
     * @param token JWT токен
     * @return имя пользователя из токена
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Проверяет валидность JWT токена.
     * 
     * @param token JWT токен для проверки
     * @return true если токен валиден, false в противном случае
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                 .verifyWith(getSigningKey())
                 .build()
                 .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
