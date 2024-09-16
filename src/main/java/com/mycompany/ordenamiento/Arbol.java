/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ordenamiento;

/**
 *
 * @author jsalazar
 */
//Clase Arbol

public class Arbol {

    Nodo raiz;

    public Arbol() {
        raiz = null;
    }

    public void recorrerPostorden() {
        recorrerPostorden(raiz);
    }

    private void recorrerPostorden(Nodo nodo) {
        if (nodo == null) {
            return;
        }

        // Recorrer el subarbol izquierdo
        recorrerPostorden(nodo.izquierdo);

        // Recorrer el subarbol derecho
        recorrerPostorden(nodo.derecho);

        // Mostrar el nodo (operando u operador)
        if (nodo.operador == ' ') {
            System.out.print(nodo.valor + " ");
        } else {
            System.out.print(nodo.operador + " ");
        }
    }

    public void recorrerPolaca() {
        recorrerPolaca(raiz);
    }

    private void recorrerPolaca(Nodo nodo) {
        if (nodo == null) {
            return;
        }

        // Mostrar el nodo (operando u operador)
        if (nodo.operador == ' ') {
            System.out.print(nodo.valor + " ");
        } else {
            System.out.print(nodo.operador + " ");
        }

        // Recorrer el subarbol izquierdo
        recorrerPolaca(nodo.izquierdo);

        // Recorrer el subarbol derecho
        recorrerPolaca(nodo.derecho);
    }

    public void imprimirArbol() {
        imprimirArbol(raiz, 0);
    }

    private void imprimirArbol(Nodo nodo, int espacio) {
        if (nodo == null) {
            return;
        }

        espacio += 10;

        imprimirArbol(nodo.derecho, espacio);

        System.out.print("\n");
        for (int i = 10; i < espacio; i++) {
            System.out.print(" ");
        }
        if (nodo.operador == ' ') {
            System.out.print(nodo.valor + "\n");
        } else {
            System.out.print(nodo.operador + "\n");
        }

        imprimirArbol(nodo.izquierdo, espacio);
    }
}

