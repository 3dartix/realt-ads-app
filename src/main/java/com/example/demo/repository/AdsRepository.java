package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdsRepository extends JpaRepository<AdData, String> {
    Optional<AdData> findAdDataByLink(String link);
    List<AdData> findAdDataByUpdateAfter(LocalDateTime data);
}
