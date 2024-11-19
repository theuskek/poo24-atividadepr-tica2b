package com.project.kanban.services;

import com.project.kanban.cliente.Tarefa;
import com.project.kanban.cliente.TarefaRepository;
import com.project.kanban.cliente.Priority;
import com.project.kanban.cliente.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;

    public TarefaService(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    public List<Tarefa> getAllTasks() {
        return tarefaRepository.findAll();
    }

    public Tarefa createTask(Tarefa tarefa) {
        tarefa.setStatus(Status.TODO);
        tarefa.setCreatedAt(LocalDate.now());
        return tarefaRepository.save(tarefa);
    }

    public Optional<Tarefa> getTaskById(Long id) {
        return tarefaRepository.findById(id);
    }

    public Tarefa updateTask(Tarefa tarefa) {
        return tarefaRepository.save(tarefa);
    }

    public void deleteTask(Long id) {
        tarefaRepository.deleteById(id);
    }

    @Transactional
    public Tarefa moveTask(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada"));
        switch (tarefa.getStatus()) {
            case Status.TODO:
                tarefa.setStatus(Status.DOING);
                break;
            case Status.DOING:
                tarefa.setStatus(Status.DONE);
                break;
            case Status.DONE:
                throw new IllegalStateException("Tarefa já concluída");
        }
        return tarefa;
    }

    public List<Tarefa> getTasksSortedByPriority(Status status) {
        return tarefaRepository.findByStatus(status).stream()
                .sorted(Comparator.comparing(Tarefa::getPriority))
                .collect(Collectors.toList());
    }

    public List<Tarefa> filterTasks(String priority, String deadline) {
        return tarefaRepository.findAll().stream()
                .filter(tarefa -> {
                    if (priority == null) return true;
                    try {
                        return tarefa.getPriority() == Priority.valueOf(priority.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                })
                .filter(tarefa -> {
                    if (deadline == null) return true;
                    LocalDate parsedDeadline = LocalDate.parse(deadline);
                    return tarefa.getDeadline() != null && tarefa.getDeadline().isBefore(parsedDeadline);
                })
                .collect(Collectors.toList());
    }


    public Map<Status, List<Tarefa>> generateReport() {
        Map<Status, List<Tarefa>> report = new HashMap<>();

        List<Status> columns = List.of(Status.TODO, Status.DOING);
        for (Status column : columns) {
            List<Tarefa> overdueTarefas = tarefaRepository.findByStatus(column).stream()
                    .filter(tarefa -> tarefa.getDeadline() != null)
                    .filter(tarefa -> tarefa.getDeadline().isBefore(LocalDateTime.now().toLocalDate()))
                    .collect(Collectors.toList());

            report.put(column, overdueTarefas);
        }

        return report;
    }


}