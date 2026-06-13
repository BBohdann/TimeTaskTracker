package com.example.TaskService.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "subtasks")
@EntityListeners(AuditingEntityListener.class)
public class Subtask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @NotBlank
    @Size(max = 255)
    @Column(name = "subtask_name")
    private String subtaskName;

    @Size(max = 2000)
    private String description;

    @Column(name = "created_time")
    @CreatedDate
    private Instant createdTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Min(0)
    @Column(name = "time_to_spend")
    private Integer timeToSpend;

    @Min(0)
    @Column(name = "time_spent", columnDefinition = "INT DEFAULT 0")
    private Integer timeSpent = 0;

    @Column(name = "is_complete", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isComplete = false;
}
