package com.project.kanban.cliente;

import com.project.kanban.cliente.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    List<Tarefa> findByStatus(Status status);
}