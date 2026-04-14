package com.example.demo_basic.controller;

import com.example.demo_basic.model.entity.Cliente;
import com.example.demo_basic.repository.ClienteRepository;
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
     * Endpoint para obtener un cliente específico por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para actualizar los datos de un cliente existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Long id, @RequestBody Cliente detallesCliente) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setNombre(detallesCliente.getNombre());
                    cliente.setTelefono(detallesCliente.getTelefono());
                    // No actualizamos puntos de lealtad aquí para evitar trampas, o depende de tu regla
                    return ResponseEntity.ok(clienteRepository.save(cliente));
                })
                .orElse(ResponseEntity.notFound().build());
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
