package com.agroorbit.api.controller;

import com.agroorbit.api.dto.LoginRequestDTO;
import com.agroorbit.api.dto.LoginResponseDTO;
import com.agroorbit.api.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticação e geração de token JWT")
public class AuthController {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // Usuários mockados — em produção viriam do banco
    private static final Map<String, String[]> USUARIOS = Map.of(
            "admin@ford.com",    new String[]{ "$2a$10$7QfkPNMBVpBmYlHkL8B0FOx7F5QZq5Qx5Qx5Qx5Qx5Qx5Qx5Qx5Q", "ADMIN" },
            "analista@ford.com", new String[]{ "$2a$10$7QfkPNMBVpBmYlHkL8B0FOx7F5QZq5Qx5Qx5Qx5Qx5Qx5Qx5Qx5Q", "ANALISTA" },
            "gerente@ford.com",  new String[]{ "$2a$10$7QfkPNMBVpBmYlHkL8B0FOx7F5QZq5Qx5Qx5Qx5Qx5Qx5Qx5Qx5Q", "GERENTE" }
    );

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica o usuário e retorna um token JWT com expiração de 24h")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Tentativa de login: {}", request.getEmail());

        String[] userData = USUARIOS.get(request.getEmail());

        // Credenciais inválidas — mensagem genérica (não revela se email existe)
        if (userData == null) {
            log.warn("[SECURITY] Login falhou para email: {}", request.getEmail());
            return ResponseEntity.status(401).body(
                    Map.of("erro", "Credenciais inválidas")
            );
        }

        // Para simplificar o mock: aceita senha "ford2026" para todos
        if (!"ford2026".equals(request.getSenha())) {
            log.warn("[SECURITY] Senha incorreta para: {}", request.getEmail());
            return ResponseEntity.status(401).body(
                    Map.of("erro", "Credenciais inválidas")
            );
        }

        String role = userData[1];
        String token = jwtService.generateToken(request.getEmail(), role);

        log.info("Login realizado: {} | role={}", request.getEmail(), role);

        return ResponseEntity.ok(LoginResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .email(request.getEmail())
                .role(role)
                .expiresIn(86400000L)
                .build());
    }
}