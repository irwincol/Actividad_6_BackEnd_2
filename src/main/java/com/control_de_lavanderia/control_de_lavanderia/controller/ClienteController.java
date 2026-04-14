package com.control_de_lavanderia.control_de_lavanderia.controller;

import com.control_de_lavanderia.control_de_lavanderia.model.Cliente;
import com.control_de_lavanderia.control_de_lavanderia.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de Cliente (Capa de Presentación / API)
 * Aquí recibimos las peticiones HTTP (GET, POST) referentes a los clientes.
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    // Inyectamos el repositorio para poder guardar y buscar clientes en la BD
    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Endpoint para crear un cliente nuevo.
     * @param cliente Datos del cliente en formato JSON
     * @return El cliente creado con su ID y Puntos de Lealtad inicializados
     */
    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        // Aseguramos que si no envían puntos, inicie en cero (0)
        if (cliente.getPuntosLealtad() == null) {
            cliente.setPuntosLealtad(0);
        }
        
        // Guardamos en Base de Datos (Prisma/PostgreSQL)
        Cliente nuevoCliente = clienteRepository.save(cliente);
        
        // Retornamos estado HTTP 200 (OK) con el cliente guardado
        return ResponseEntity.ok(nuevoCliente);
    }

    /**
     * Endpoint para obtener la lista de todos los clientes.
     */
    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerClientes() {
        return ResponseEntity.ok(clienteRepository.findAll());
    }

    /**
     * Endpoint para eliminar un cliente por su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        if (!clienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
