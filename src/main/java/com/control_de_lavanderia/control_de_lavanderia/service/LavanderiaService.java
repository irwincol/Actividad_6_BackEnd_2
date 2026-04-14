package com.control_de_lavanderia.control_de_lavanderia.service;

import com.control_de_lavanderia.control_de_lavanderia.model.Cliente;
import com.control_de_lavanderia.control_de_lavanderia.model.Orden;
import com.control_de_lavanderia.control_de_lavanderia.model.Prenda;
import com.control_de_lavanderia.control_de_lavanderia.model.enums.TipoPrenda;
import com.control_de_lavanderia.control_de_lavanderia.repository.ClienteRepository;
import com.control_de_lavanderia.control_de_lavanderia.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Capa de Servicios (Reglas del Negocio)
 * Aquí colocamos todo lo que nos pidieron de cálculos (horas, puntos,
 * descuentos).
 */
@Service
public class LavanderiaService {

    // Secciones de Tarifas Constantes (Se podrían sacar a una base de datos más
    // adelante)
    private static final double PRECIO_CAMISA = 15000.0;
    private static final double PRECIO_PANTALON = 20000.0;

    // Los repositorios se utilizan para conectarse e insertar en la base de datos
    // de Prisma
    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * @Transactional asegura que si algo falla, no se guarde nada a la mitad,
     *                protegiendo que tanto Cliente como Orden se guarden completos
     *                o "hagan Rollback".
     */
    @Transactional
    public Orden procesarNuevaOrden(Orden orden) {

        // PASO EXTRA: Consultar base de datos para obtener los puntos reales del
        // cliente
        // ya que desde Postman puede que solo nos manden { "cliente": { "id": 1 } }
        Cliente cliente = clienteRepository.findById(orden.getCliente().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no existe"));
        orden.setCliente(cliente);
        List<Prenda> prendas = orden.getPrendas();

        // ------ LÓGICA 1: Validar máximos ------
        if (prendas == null || prendas.isEmpty()) {
            throw new IllegalArgumentException("La orden debe tener al menos una prenda.");
        }
        if (prendas.size() > 20) {
            throw new IllegalArgumentException("No se aceptan más de 20 prendas en un solo pedido.");
        }

        // Si desde la API no envían "cuando" se recibió, tomamos la fecha/hora actual
        // del servidor
        if (orden.getFechaRecibido() == null) {
            orden.setFechaRecibido(LocalDateTime.now());
        }

        // ------ LÓGICA 2: Calcular fecha de Entrega (24hrs / 48 hrs) ------
        if (prendas.size() < 5) {
            orden.setFechaEntregaEstimada(orden.getFechaRecibido().plusHours(24));
        } else {
            orden.setFechaEntregaEstimada(orden.getFechaRecibido().plusHours(48));
        }

        // ------ LÓGICA 3: Calcular el costo total ($) ------
        double total = 0.0;
        // Iniciamos la variable de prenda más barata en el valor más alto posible,
        // para que la primera prenda que lea tome ese puesto, e irla comparando.
        double prendaMasBarata = Double.MAX_VALUE;

        for (Prenda prenda : prendas) {
            double precioActual = 0.0;
            if (prenda.getTipo() == TipoPrenda.CAMISA) {
                precioActual = PRECIO_CAMISA;
            } else if (prenda.getTipo() == TipoPrenda.PANTALON) {
                precioActual = PRECIO_PANTALON;
            }

            total += precioActual; // Sumamos la prenda al carrito

            // Si la prenda que leemos es más barata que el récord anterior, actualizamos la
            // variable
            if (precioActual < prendaMasBarata) {
                prendaMasBarata = precioActual;
            }
        }

        // ------ LÓGICA 4: Aplicar descuentos si el cliente tiene 50 puntos o más
        // ------
        if (cliente.getPuntosLealtad() != null && cliente.getPuntosLealtad() >= 50) {
            // Se le resta el valor de la prenda más barata (Ej: -15.000 de una Camisa)
            total -= prendaMasBarata;
            // Cobramos / Canjeamos los 50 puntos de su billetera virtual
            cliente.setPuntosLealtad(cliente.getPuntosLealtad() - 50);
        }

        orden.setTotal(total); // Fijamos el precio final

        // ------ LÓGICA 5: Otorgar Nuevos Puntos (1 x c/ $10.000) ------
        int puntosGanados = (int) (total / 10000); // division exacta, ej: 35.000 / 10K = 3
        int puntosActuales = cliente.getPuntosLealtad() != null ? cliente.getPuntosLealtad() : 0;
        cliente.setPuntosLealtad(puntosActuales + puntosGanados);

        // PASO FINAL: Guardar ambos cambios en PostgreSQL y retornar todo al
        // Controlador
        clienteRepository.save(cliente);
        return ordenRepository.save(orden);
    }
}
