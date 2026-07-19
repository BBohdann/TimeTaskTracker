package com.example.TaskService.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "work_sessions")
public class WorkSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "subtask_id")
    private Long subtaskId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WorkSessionStatus status;

    @OneToMany(
            mappedBy = "workSession",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<WorkSessionInterval> intervals = new ArrayList<>();
}
