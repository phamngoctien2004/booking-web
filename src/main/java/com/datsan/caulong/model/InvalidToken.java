package com.datsan.caulong.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class InvalidToken {
    @Id
    private String id;

    private LocalDateTime logoutTime;
}
