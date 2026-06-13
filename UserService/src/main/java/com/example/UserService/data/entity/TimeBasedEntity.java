package com.example.UserService.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public abstract class TimeBasedEntity {
    @LastModifiedDate
    @Column(name = "last_updated_time", nullable = false)
    Instant lastUpdatedDate;

    @CreatedDate
    @Column(name = "created_time", nullable = false, updatable = false)
    Instant createdDate;
}
