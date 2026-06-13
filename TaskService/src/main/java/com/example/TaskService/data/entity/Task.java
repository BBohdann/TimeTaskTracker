package com.example.TaskService.data.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
public class Task {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotNull
        @Column(name = "user_id")
        private Long userId;

        @NotBlank
        @Size(max = 255)
        @Column(name = "task_name")
        private String taskName;

        @Size(max = 2000)
        private String description;

        @CreatedDate
        @Column(name = "created_time")
        private Instant createdTime;

        @Column(name = "end_time")
        private Instant endTime;

        @Min(1)
        @Column(name = "time_to_spend")
        private Integer timeToSpend;

        @Min(0)
        @Column(name = "time_spent", columnDefinition = "INT DEFAULT 0")
        private Integer timeSpent = 0;

        @Column(name = "is_complete", columnDefinition = "BOOLEAN DEFAULT FALSE")
        private Boolean isComplete = false;

        @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
        private List<Subtask> subtasks;
}
