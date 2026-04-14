package com.example.demo_basic.service;

import com.example.demo_basic.model.entity.Orden;
import com.example.demo_basic.model.entity.Prenda;
import com.example.demo_basic.model.enums.TipoPrenda;
import org.springframework.stereotype.Service;

@Service
public class AplicacionDescuentoLealtad {

    
    public boolean procesar(Orden orden) {
        if (orden.getCliente() == null || orden.getCliente().getPuntosLealtad() == null) {
            return true; // No se puede aplicar si no hay cliente o los puntos son nulos
        }

        // Regla: Si el cliente tiene 50 puntos, aplicará descuento total a la prenda más barata.
        if (orden.getCliente().getPuntosLealtad() >= 50 && !orden.getPrendas().isEmpty()) {
            
            // Buscamos el precio más barato
            double tarifaMasBarata = Double.MAX_VALUE;
            
            for (Prenda prenda : orden.getPrendas()) {
                double tarifaActual = (prenda.getTipo() == TipoPrenda.CAMISA) ? 15000.0 : 20000.0;
                if (tarifaActual < tarifaMasBarata) {
                    tarifaMasBarata = tarifaActual;
                }
            }
            
            // Aplicamos el descuento al total de la orden
            double nuevoTotal = orden.getTotal() - tarifaMasBarata;
            if (nuevoTotal < 0) {
                nuevoTotal = 0;
            }
            orden.setTotal(nuevoTotal);
            
            // Consumimos los puntos del cliente (asumiendo que se gastan al redimir el descuento)
            orden.getCliente().setPuntosLealtad(orden.getCliente().getPuntosLealtad() - 50);
        }
        
        return true;
    }
}
