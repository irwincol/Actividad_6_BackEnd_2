package com.example.demo_basic.service;

import com.example.demo_basic.model.entity.Orden;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AsignacionFechaEntrega {

    
    public boolean procesar(Orden orden) {
        if (orden.getFechaRecibido() == null) {
            orden.setFechaRecibido(LocalDateTime.now());
        }

        int cantidadPrendas = orden.getPrendas().size();
        
        // Regla: Asignar fecha de entrega: +24 horas si son menos de 5 prendas, +48 horas si son más (incluye 5).
        if (cantidadPrendas < 5) {
            orden.setFechaEntregaEstimada(orden.getFechaRecibido().plusHours(24));
        } else {
            orden.setFechaEntregaEstimada(orden.getFechaRecibido().plusHours(48));
        }
        
        return true;
    }
}
