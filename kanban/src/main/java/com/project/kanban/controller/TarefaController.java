package com.project.kanban.controller;

import com.project.kanban.cliente.Tarefa;
import com.project.kanban.services.TarefaService;
import com.project.kanban.cliente.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @GetMapping
    public ResponseEntity<List<Tarefa>> getAllTasks() {
        try {
            List<Tarefa> tarefas = tarefaService.getAllTasks();
            return ResponseEntity.ok(tarefas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Tarefa tarefa) {
        try {
            Tarefa createdTarefa = tarefaService.createTask(tarefa);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTarefa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar a tarefa");
        }
    }

    @PutMapping("/move/{id}")
    public ResponseEntity<?> moveTask(@PathVariable Long id) {
        try {
            Tarefa movedTarefa = tarefaService.moveTask(id);
            return ResponseEntity.ok(movedTarefa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao mover a tarefa");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Tarefa updatedTarefa) {
        try {
            Tarefa existingTarefa = tarefaService.getTaskById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada"));
            existingTarefa.setTitle(updatedTarefa.getTitle());
            existingTarefa.setDescription(updatedTarefa.getDescription());
            existingTarefa.setPriority(updatedTarefa.getPriority());
            existingTarefa.setDeadline(updatedTarefa.getDeadline());
            Tarefa updated = tarefaService.updateTask(existingTarefa);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar a tarefa");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            tarefaService.deleteTask(id);
            return ResponseEntity.ok("Tarefa excluída com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir a tarefa");
        }
    }

    @GetMapping("/sorted/{status}")
    public ResponseEntity<List<Tarefa>> getTasksSortedByPriority(@PathVariable Status status) {
        return ResponseEntity.ok(tarefaService.getTasksSortedByPriority(status));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Tarefa>> filterTasks(
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String deadline) {
        List<Tarefa> filteredTarefas = tarefaService.filterTasks(priority, deadline);
        return ResponseEntity.ok(filteredTarefas);
    }

    @GetMapping("/report")
    public ResponseEntity<Map<Status, List<Tarefa>>> generateReport() {
        return ResponseEntity.ok(tarefaService.generateReport());
    }
}
