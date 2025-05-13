package com.datsan.caulong.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Token {
    @Id
    private String id;
    private String email;
    private String type;
    private LocalDateTime logoutTime;
    private boolean isValid;
}
