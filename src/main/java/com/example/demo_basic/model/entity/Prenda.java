package com.example.demo_basic.model.entity;

import com.example.demo_basic.model.enums.TipoPrenda;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prendas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prenda extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoPrenda tipo;

    @Column(name = "color", length = 30)
    private String color;

    @Column(name = "instrucciones_especiales", length = 200)
    private String instruccionesEspeciales;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;
}
