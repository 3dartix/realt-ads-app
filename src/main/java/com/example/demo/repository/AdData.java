package com.example.demo.repository;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class AdData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "price", nullable = false)
    private String price;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "link", nullable = false)
    private String link;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime create;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime update;

    @Column(name = "from_agent", nullable = false)
    private Boolean fromAgent;
}
