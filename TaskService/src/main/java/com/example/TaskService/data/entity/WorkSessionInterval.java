package com.example.TaskService.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "work_session_intervals")
public class WorkSessionInterval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "work_session_id",
            nullable = false
    )
    private WorkSession workSession;


    @Column(name = "started_at", nullable = false)
    private Instant startedAt;


    @Column(name = "finished_at")
    private Instant finishedAt;


    @Column(name = "duration")
    private Long duration = 0L;
}