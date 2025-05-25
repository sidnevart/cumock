package com.example.cumock.security;

import com.example.cumock.model.User;
import com.example.cumock.repository.UserRepository;
import com.example.cumock.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;


    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Извлекаем заголовок Authorization
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 2. Проверяем, что заголовок есть и начинается с Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Извлекаем сам токен (без "Bearer ")
        String token = authHeader.substring(7);

        try {
            // 4. Парсим все Claims из токена
            Claims claims = jwtService.extractAllClaims(token);

            if (claims != null && claims.getSubject() != null) {
                String email = claims.getSubject(); // subject = email
                User user = userRepository.findByEmail(email).orElse(null);

                if (user == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                // 5. Преобразуем роль в GrantedAuthority — обязательно для @PreAuthorize("hasRole(...)")
                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority(user.getRole().name()) // ROLE_ADMIN или ROLE_USER
                );

                // 6. Создаём объект Authentication с email и правами
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. Устанавливаем в SecurityContext — теперь доступно в контроллерах
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            // 8. Если токен невалиден — печатаем и возвращаем 401
            System.out.println("JWT is invalid or expired: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 9. Пропускаем дальше в цепочке
        filterChain.doFilter(request, response);
    }
}
