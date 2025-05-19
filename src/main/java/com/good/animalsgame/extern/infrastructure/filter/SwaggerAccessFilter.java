package com.good.animalsgame.extern.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Фильтр доступа к Swagger
 */
@Component
public class SwaggerAccessFilter extends OncePerRequestFilter {

    /**
     * Логин
     */
    private String login;

    /**
     * Пароль
     */
    private String password;

    public SwaggerAccessFilter(@Value("${swagger.access.login}") String login,
                               @Value("${swagger.access.password}") String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String URI = request.getRequestURI();

        if (URI.startsWith("/swagger-ui") || URI.startsWith("/v3/api-docs")) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Basic ")) {
                String base64Credentials = authHeader.substring("Basic ".length());
                String decodedCredentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
                String[] credentials = decodedCredentials.split(":", 2);

                if (credentials.length == 2 && credentials[0].equals(login) && credentials[1].equals(password)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            response.setHeader("WWW-Authenticate", "Basic realm=\"Swagger\"");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else
            filterChain.doFilter(request, response);
    }
}
