package com.example.UserService.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Getter
@Setter
@MappedSuperclass
public class TimeBasedEntity {
    @CreatedDate
    @Column(name = "last_updated_time", nullable = false)
    LocalDate lastUpdatedDate;

    @CreatedDate
    @Column(name = "created_time", nullable = false, updatable = false)
    LocalDate createdDate;
}
