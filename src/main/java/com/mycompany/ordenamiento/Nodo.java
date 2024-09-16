package com.mycompany.ordenamiento;

public class Nodo {
    int valor;
    char operador;
    Nodo izquierdo, derecho;

    // Constructor para valores numéricos
    public Nodo(int valor) {
        this.valor = valor;
        this.operador = ' ';
        this.izquierdo = null;
        this.derecho = null;
    }

    // Constructor para operadores
    public Nodo(char operador) {
        this.valor = 0;
        this.operador = operador;
        this.izquierdo = null;
        this.derecho = null;
    }

    // Constructor para operadores con hijos
    public Nodo(char operador, Nodo izquierdo, Nodo derecho) {
        this.valor = 0;
        this.operador = operador;
        this.izquierdo = izquierdo;
        this.derecho = derecho;
    }
}
