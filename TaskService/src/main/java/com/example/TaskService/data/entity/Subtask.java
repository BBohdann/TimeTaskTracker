package com.example.TaskService.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "subtasks")
@Data
public class Subtask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonBackReference
    private Task task;

    @NotBlank
    @Size(max = 255)
    @Column(name = "subtask_name")
    private String subtaskName;

    @Size(max = 2000)
    private String description;

    @NotNull
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Min(0)
    @Column(name = "time_to_spend")
    private Integer timeToSpend;

    @Min(0)
    @Column(name = "time_spent", columnDefinition = "INT DEFAULT 0")
    private Integer timeSpent = 0;

    @Column(name = "is_complete", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isComplete = false;
}
