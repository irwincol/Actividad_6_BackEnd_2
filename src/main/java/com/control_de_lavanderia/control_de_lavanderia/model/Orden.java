package com.control_de_lavanderia.control_de_lavanderia.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ordenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Orden extends BaseEntity {

    @Column(name = "fecha_recibido", nullable = false)
    private LocalDateTime fechaRecibido;

    @Column(name = "fecha_entrega_estimada", nullable = false)
    private LocalDateTime fechaEntregaEstimada;

    @Column(name = "total", nullable = false)
    private Double total;

    /**
     * RELACIÓN (N:1): Muchas Órdenes pertenecen a UN Cliente.
     * - @ManyToOne: Indica la relación de "Muchos a Uno".
     * - @JoinColumn: Especifica que la columna "cliente_id" en la base de datos 
     *   se usará como clave foránea (Foreign Key) para enlazar ambos registros físicamente.
     */
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /**
     * RELACIÓN (1:N): Una Orden puede tener MUCHAS Prendas (Ej: 3 camisas y 1 pantalón).
     * - mappedBy = "orden": Indica que la clase Prenda es dueña de esta relación
     *   y la gestiona usando su atributo llamado "orden".
     * - cascade = CascadeType.ALL: Si se cancela/elimina la orden, se eliminan también
     *   todas las prendas de la base datos que estaban asociadas a su recibo.
     */
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL)
    private List<Prenda> prendas;

}
