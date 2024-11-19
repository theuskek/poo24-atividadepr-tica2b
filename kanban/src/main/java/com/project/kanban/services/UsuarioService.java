package com.project.kanban.services;

import com.project.kanban.cliente.Usuario;
import com.project.kanban.cliente.UsuarioRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Register a new user
    public Usuario registerUser(Usuario user) {
        if (usuarioRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Usuário já cadastrado!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encode the password
        return usuarioRepository.save(user);
    }

    // Authenticate a user and return the user entity
    public Usuario authenticate(String username, String password) {
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Credenciais inválidas!");
        }
        return user;
    }
}
