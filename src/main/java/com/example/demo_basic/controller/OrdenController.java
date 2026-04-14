package com.example.demo_basic.controller;

import com.example.demo_basic.model.entity.Cliente;
import com.example.demo_basic.model.entity.Prenda;
import com.example.demo_basic.service.AplicacionDescuentoLealtad;
import com.example.demo_basic.service.AsignacionFechaEntrega;
import com.example.demo_basic.service.CalculoTarifaTotal;
import com.example.demo_basic.service.SumaPuntosLealtad;
import com.example.demo_basic.service.ValidacionCantidadPrendas;
import com.example.demo_basic.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo_basic.repository.OrdenRepository;
import com.example.demo_basic.model.entity.Orden;

/**
 * Controlador de Ordenes (Capa de Presentación / API)
 * Gestiona el ingreso de pedidos utilizando servicios de lógica de negocio individuales.
 */
@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    @Autowired
    private OrdenRepository ordenRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ValidacionCantidadPrendas validacionCantidadPrendas;

    @Autowired
    private CalculoTarifaTotal calculoTarifaTotal;

    @Autowired
    private AsignacionFechaEntrega asignacionFechaEntrega;

    @Autowired
    private AplicacionDescuentoLealtad aplicacionDescuentoLealtad;

    @Autowired
    private SumaPuntosLealtad sumaPuntosLealtad;

    /**
     * Endpoint para recibir y crear una orden nueva con sus prendas.
     * @param orden La entidad Orden que viene del Body en Postman (JSON)
     */
    @PostMapping
    public ResponseEntity<?> crearOrden(@RequestBody Orden orden) {
        
        // 0. Recuperar cliente de la base de datos
        if (orden.getCliente() != null && orden.getCliente().getId() != null) {
            Cliente clienteDb = clienteRepository.findById(orden.getCliente().getId()).orElse(null);
            if (clienteDb == null) {
                return ResponseEntity.badRequest().body("Error: El cliente con ID " + orden.getCliente().getId() + " no existe.");
            }
            orden.setCliente(clienteDb);
        } else {
            return ResponseEntity.badRequest().body("Error: No se proporcionó un ID de cliente válido.");
        }

        // 1. Relación bidireccional
        if (orden.getPrendas() != null) {
            for (Prenda p : orden.getPrendas()) {
                p.setOrden(orden);
            }
        }
        
        // 2. Ejecutar cada lógica de negocio desde su clase

        // Validar cantidad de prendas
        boolean esValida = validacionCantidadPrendas.procesar(orden);
        if (!esValida) {
            return ResponseEntity.badRequest().body("Error: La orden supera el máximo de prendas permitido (20).");
        }

        // Calcular la tarifa base
        calculoTarifaTotal.procesar(orden);
        
        // Aplicar descuentos por lealtad
        aplicacionDescuentoLealtad.procesar(orden);
        
        // Sumar puntos de lealtad basados en la tarifa final
        sumaPuntosLealtad.procesar(orden);
        
        // Asignar fechas
        asignacionFechaEntrega.procesar(orden);

        // 3. Persistir cambios 
        // Si el cliente modificó sus puntos, hay que guardarlo
        if (orden.getCliente() != null && orden.getCliente().getId() != null) {
            clienteRepository.save(orden.getCliente());
        }
        
        Orden nuevaOrden = ordenRepository.save(orden);
        
        // 4. Devolvemos la orden al cliente (Postman)
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

    /**
     * Endpoint para obtener todas las ordenes.
     */
    @GetMapping
    public ResponseEntity<Iterable<Orden>> obtenerOrdenes() {
        return ResponseEntity.ok(ordenRepository.findAll());
    }

    /**
     * Endpoint para obtener una orden específica por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Orden> obtenerOrdenPorId(@PathVariable Long id) {
        return ordenRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para actualizar una orden existente (Ej: cambiar su fecha de entrega o total).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Orden> actualizarOrden(@PathVariable Long id, @RequestBody Orden detallesOrden) {
        return ordenRepository.findById(id)
                .map(orden -> {
                    if (detallesOrden.getFechaEntregaEstimada() != null) {
                        orden.setFechaEntregaEstimada(detallesOrden.getFechaEntregaEstimada());
                    }
                    if (detallesOrden.getTotal() != null) {
                        orden.setTotal(detallesOrden.getTotal());
                    }
                    return ResponseEntity.ok(ordenRepository.save(orden));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
