package com.example.demo_basic.repository;

import com.example.demo_basic.model.entity.Prenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrendaRepository extends JpaRepository<Prenda, Long> {
}
