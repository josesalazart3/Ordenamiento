/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ordenamiento;

public class Nodo {

    char operador;
    int valor;
    Nodo izquierdo, derecho;

    public Nodo(char operador) {
        this.operador = operador;
        this.valor = -1; // Valor predeterminado para operadores
        this.izquierdo = this.derecho = null;
    }

    public Nodo(int valor) {
        this.operador = ' ';
        this.valor = valor;
        this.izquierdo = this.derecho = null;
    }
}