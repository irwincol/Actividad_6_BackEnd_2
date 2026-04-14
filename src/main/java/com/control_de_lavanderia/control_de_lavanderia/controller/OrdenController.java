package com.control_de_lavanderia.control_de_lavanderia.controller;

import com.control_de_lavanderia.control_de_lavanderia.model.Orden;
import com.control_de_lavanderia.control_de_lavanderia.model.Prenda;
import com.control_de_lavanderia.control_de_lavanderia.service.LavanderiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.control_de_lavanderia.control_de_lavanderia.repository.OrdenRepository;

/**
 * Controlador de Ordenes (Capa de Presentación / API)

 * Gestiona el ingreso de pedidos validando los datos a través del Service.
 */
@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    // Inyectamos el servicio, donde está toda la "inteligencia" y las reglas de negocio
    @Autowired
    private LavanderiaService lavanderiaService;

    @Autowired
    private OrdenRepository ordenRepository;

    /**
     * Endpoint para recibir y crear una orden nueva con sus prendas.
     * @param orden La entidad Orden que viene del Body en Postman (JSON)
     */
    @PostMapping
    public ResponseEntity<Orden> crearOrden(@RequestBody Orden orden) {
        
        // 1. Verificar si nos enviaron prendas
        // Es importante hacer el enlace (setOrden) aquí para que Hibernate / Base de datos
        // entienda a qué orden pertenece cada prenda, ya que es una relación Bidireccional.
        if (orden.getPrendas() != null) {
            for (Prenda p : orden.getPrendas()) {
                p.setOrden(orden);
            }
        }
        
        // 2. Mandamos la orden cruda al Servicio para que procese fechas, totales, descuentos y puntos.
        // El servicio nos retorna la orden ya con cálculos listos y guardada en la BD.
        Orden nuevaOrden = lavanderiaService.procesarNuevaOrden(orden);
        
        // 3. Devolvemos la orden al cliente (Postman) para que vea sus cobros y fechas calculadas
        return ResponseEntity.ok(nuevaOrden);
    }

    /**
     * Endpoint para eliminar una orden por su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOrden(@PathVariable Long id) {
        if (!ordenRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        ordenRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
