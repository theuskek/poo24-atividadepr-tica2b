package com.project.kanban.controller;

import com.project.kanban.cliente.Usuario;
import com.project.kanban.cliente.Usuario;
import com.project.kanban.services.JwtService;
import com.project.kanban.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AutenticadorController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    @Autowired
    public AutenticadorController(UsuarioService usuarioService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario user) {
        try {
            usuarioService.registerUser(user);
            return ResponseEntity.ok("Usuário registrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            Usuario user = usuarioService.authenticate(credentials.get("username"), credentials.get("password"));
            String token = jwtService.generateToken(user.getUsername());
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (IllegalArgumentException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
