package com.mycompany.ordenamiento;

import java.util.HashMap;
import java.util.Map;

public class Automata {
    // Enumeracion de los estados
    public enum Estado {
        q0, q1, q2, q3, ERROR
    }

    // Enumeracion de los símbozlos del alfabeto
    public enum Simbolo {
        VARIABLE, ASIGNACION, OPERADOR, NUMERO, PARENTESIS, DESCONOCIDO
    }

    // Mapa para la funcion de transicion
    private final Map<Estado, Map<Simbolo, Estado>> transiciones;

    // Estado actual del automata
    private Estado estadoActual;

    // Constructor
    public Automata() {
        // Inicializar el mapa de transiciones
        transiciones = new HashMap<>();

        // Definir las transiciones
        for (Estado estado : Estado.values()) {
            transiciones.put(estado, new HashMap<>());
        }

        // Definir las transiciones para cada estado
        definirTransiciones();
        estadoActual = Estado.q0; // Estado inicial
    }

    // Método para definir las transiciones
    private void definirTransiciones() {
        // Transiciones para una expresion matematica simple
        transiciones.get(Estado.q0).put(Simbolo.VARIABLE, Estado.q1);
        transiciones.get(Estado.q1).put(Simbolo.OPERADOR, Estado.q2);
        transiciones.get(Estado.q2).put(Simbolo.VARIABLE, Estado.q1);
        transiciones.get(Estado.q2).put(Simbolo.NUMERO, Estado.q3);
        transiciones.get(Estado.q3).put(Simbolo.OPERADOR, Estado.q2);
        transiciones.get(Estado.q3).put(Simbolo.PARENTESIS, Estado.q3);

        // Transicion desde cualquier estado al estado ERROR con símbolo DESCONOCIDO
        for (Estado estado : Estado.values()) {
            transiciones.get(estado).put(Simbolo.DESCONOCIDO, Estado.ERROR);
        }
    }

    // Método para procesar un símbolo y transitar al siguiente estado
    public void procesarSimbolo(Simbolo simbolo) {
        Estado siguienteEstado = transiciones.get(estadoActual).getOrDefault(simbolo, Estado.ERROR);
        if (siguienteEstado == Estado.ERROR) {
            // Manejo de error si la transicion no esta definida
            // Puedes dejar este comentario para futuras referencias
            // System.out.println("Transición no válida desde " + estadoActual + " con símbolo " + simbolo);
        } else {
            // Puedes dejar este comentario para futuras referencias
            // System.out.println("Transición a estado: " + siguienteEstado);
        }
        estadoActual = siguienteEstado;
    }

    // Método para verificar si el estado actual es final
    public boolean esEstadoFinal() {
        return estadoActual == Estado.q1 || estadoActual == Estado.q3; // Estados finales
    }
}
