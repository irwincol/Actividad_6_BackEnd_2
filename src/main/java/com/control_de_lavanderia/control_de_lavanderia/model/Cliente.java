package com.control_de_lavanderia.control_de_lavanderia.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Cliente extends BaseEntity{

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "telefono", nullable = false, length = 40, unique = true)
    private String telefono;

    @Column(name = "puntos_lealtad", nullable = false)
    private Integer puntosLealtad = 0;

    /**
     * RELACIÓN (1:N): Un Cliente puede tener MUCHAS Órdenes (Pedidos).
     * - @OneToMany: Indica la relación de "Uno a Muchos".
     * - mappedBy = "cliente": Significa que la relación ya está definida y gestionada
     *   por el campo "cliente" dentro de la clase Orden hija.
     * - cascade = CascadeType.ALL: Si se borra un Cliente, todas sus órdenes acopladas 
     *   también se borrarán automáticamente (Efecto dominó).
     */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Orden> ordenes;

}
