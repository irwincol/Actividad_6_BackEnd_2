package com.example.demo_basic.service;

import com.example.demo_basic.model.entity.Orden;
import org.springframework.stereotype.Service;

@Service
public class SumaPuntosLealtad {

    
    public boolean procesar(Orden orden) {
        if (orden.getCliente() == null || orden.getCliente().getPuntosLealtad() == null) {
            return true; 
        }

        // Regla: Sumar puntos de lealtad al cliente por cada orden (1 punto por cada $10.000).
        if (orden.getTotal() > 0) {
            int puntosGanados = (int) (orden.getTotal() / 10000);
            int nuevosPuntos = orden.getCliente().getPuntosLealtad() + puntosGanados;
            orden.getCliente().setPuntosLealtad(nuevosPuntos);
        }
        
        return true;
    }
}
