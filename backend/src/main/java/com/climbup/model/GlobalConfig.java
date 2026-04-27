package com.climbup.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "global_config")
@Getter
@Setter
public class GlobalConfig {

    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private double globalDiscount = 0.0;
}
