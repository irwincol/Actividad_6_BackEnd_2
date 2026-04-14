package com.example.demo_basic.service;

import com.example.demo_basic.model.entity.Orden;
import com.example.demo_basic.model.entity.Prenda;
import com.example.demo_basic.model.enums.TipoPrenda;
import org.springframework.stereotype.Service;

@Service
public class CalculoTarifaTotal {

    private static final double TARIFA_CAMISA = 15000.0;
    private static final double TARIFA_PANTALON = 20000.0;

    
    public boolean procesar(Orden orden) {
        double total = 0.0;
        
        for (Prenda prenda : orden.getPrendas()) {
            if (prenda.getTipo() == TipoPrenda.CAMISA) {
                total += TARIFA_CAMISA;
            } else if (prenda.getTipo() == TipoPrenda.PANTALON) {
                total += TARIFA_PANTALON;
            }
        }
        
        orden.setTotal(total);
        return true;
    }
}
