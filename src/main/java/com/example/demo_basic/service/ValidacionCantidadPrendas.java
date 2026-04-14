package com.example.demo_basic.service;

import com.example.demo_basic.model.entity.Orden;
import org.springframework.stereotype.Service;

@Service
public class ValidacionCantidadPrendas {

    
    public boolean procesar(Orden orden) {
        if (orden.getPrendas() == null || orden.getPrendas().isEmpty()) {
            return false; // Una orden debe tener prendas
        }
        
        if (orden.getPrendas().size() > 20) {
            // Regla: Validar que no se acepten más de 20 prendas en un solo pedido.
            return false;
        }
        
        return true;
    }
}
