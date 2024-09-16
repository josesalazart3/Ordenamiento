/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.ordenamiento;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class Ordenamiento {

    // Método principal proyecto arboles
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la expresion matematica con variables: ");
        String expresion = scanner.nextLine();

        // Mapa para almacenar los valores de las variables
        Map<Character, Integer> variables = new HashMap<>();
        for (char c : expresion.toCharArray()) {
            // Solicitar al usuario que ingrese el valor de cada variable encontrada en la expresion
            if (Character.isAlphabetic(c) && !variables.containsKey(c)) {
                System.out.print("Ingrese el valor para " + c + ": ");
                int valor = scanner.nextInt();
                variables.put(c, valor);
            }
        }

        // Mostrar la expresion matematica con los valores ingresados
        System.out.println("Expresion matematica con valores ingresados:");
        StringBuilder expresionConValores = new StringBuilder();
        for (char c : expresion.toCharArray()) {
            if (Character.isAlphabetic(c)) {
                expresionConValores.append(variables.get(c));
            } else {
                expresionConValores.append(c);
            }
        }
        System.out.println(expresionConValores.toString());

        // Evaluar la expresion y mostrar el resultado
        int resultado = evaluar(expresion, variables);
        System.out.println("El resultado de la expresion es: " + resultado);

        // Crear el arbol de expresiones y mostrar el recorrido postorden
        Arbol arbol = new Arbol();
        arbol.raiz = construirArbol(expresion, variables);
        System.out.println("Recorrido postorden del arbol de expresiones:");
        arbol.recorrerPostorden();

        // Mostrar el recorrido en notacion polaca del arbol de expresiones
        System.out.println("\nRecorrido en notacion polaca del arbol de expresiones:");
        arbol.recorrerPolaca();

        // Imprimir el árbol
        System.out.println("\nArbol de expresiones:");
        arbol.imprimirArbol();

       
        scanner.close();
    }

    // Método para evaluar la expresion matematica y calcular su resultado
    public static int evaluar(String expresion, Map<Character, Integer> variables) {
        // Construir el arbol de expresiones
        Arbol arbol = new Arbol();
        arbol.raiz = construirArbol(expresion, variables);

        // Evaluar el arbol de expresiones
        return evaluarArbol(arbol.raiz);
    }

    // Método para evaluar el arbol de expresiones recursivamente
    public static int evaluarArbol(Nodo raiz) {
        if (raiz == null) {
            return 0;
        }

        if (raiz.operador == ' ') {
            return raiz.valor;
        }

        int izquierdo = evaluarArbol(raiz.izquierdo);
        int derecho = evaluarArbol(raiz.derecho);

        switch (raiz.operador) {
            case '+':
                return izquierdo + derecho;
            case '-':
                return izquierdo - derecho;
            case '*':
                return izquierdo * derecho;
            case '/':
                if (derecho != 0) {
                    return izquierdo / derecho;
                } else {
                    throw new ArithmeticException("Division por cero");
                }
            default:
                return 0;
        }
    }

    // Método para construir el arbol de expresiones
    public static Nodo construirArbol(String expresion, Map<Character, Integer> variables) {
        char[] chars = expresion.toCharArray();

        Stack<Nodo> stackExpresion = new Stack<>();
        Stack<Character> stackOperadores = new Stack<>();

        boolean unario = true; // Bandera para indicar si el operador es unario

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ') {
                continue; // Saltar espacios en blanco
            }

            if (Character.isDigit(chars[i])) {
                // Si es un dígito, construir el número y agregarlo a la pila de expresiones
                StringBuilder sbuf = new StringBuilder();
                while (i < chars.length && (Character.isDigit(chars[i]) || chars[i] == '.')) {
                    sbuf.append(chars[i++]);
                }
                i--; // Retroceder para volver a leer el siguiente caracter
                int valor = Integer.parseInt(sbuf.toString());
                Nodo numero = new Nodo(valor);
                stackExpresion.push(numero);
                unario = false; // El número no es un operador unario
            } else if (chars[i] == '(') {
                // Si es un paréntesis de apertura, agregarlo a la pila de operadores
                stackOperadores.push(chars[i]);
                unario = true; // El siguiente operador puede ser unario
            } else if (chars[i] == ')') {
                // Si es un paréntesis de cierre, evaluar las operaciones dentro del paréntesis
                while (!stackOperadores.isEmpty() && stackOperadores.peek() != '(') {
                    procesarOperador(stackOperadores.pop(), stackExpresion);
                }
                stackOperadores.pop(); // Eliminar el paréntesis de apertura
                unario = false; // El paréntesis cierra una expresion, no puede ser seguido por un operador unario
            } else if (chars[i] == '+' || chars[i] == '-' || chars[i] == '*' || chars[i] == '/') {
                // Verificar si el operador es unario
                boolean esUnario = unario && (chars[i] == '+' || chars[i] == '-');
                // Evaluar las operaciones de acuerdo a la precedencia de operadores
                while (!stackOperadores.isEmpty() && (!esUnario && tienePrecedencia(chars[i], stackOperadores.peek()))) {
                    procesarOperador(stackOperadores.pop(), stackExpresion);
                }
                stackOperadores.push(chars[i]); // Agregar el operador a la pila de operadores
                unario = esUnario; // Actualizar la bandera de operador unario
            } else if (Character.isAlphabetic(chars[i])) {
                // Si es una variable, obtener su valor y agregarlo a la pila de expresiones
                if (!variables.containsKey(chars[i])) {
                    System.out.print("Ingrese el valor para " + chars[i] + ": ");
                    Scanner scanner = new Scanner(System.in);
                    int valor = scanner.nextInt();
                    scanner.close();
                    variables.put(chars[i], valor);
                }
                int valor = variables.get(chars[i]);
                Nodo variable = new Nodo(valor);
                stackExpresion.push(variable);
                unario = false; // Una variable no puede ser seguida por un operador unario
            }
        }

        // Procesar operadores restantes en la pila de operadores y expresiones
        while (!stackOperadores.isEmpty()) {
            procesarOperador(stackOperadores.pop(), stackExpresion);
        }

        // El resultado final estara en la raíz del arbol de expresiones
        return stackExpresion.pop();
    }

    // Método para procesar operadores y construir el arbol de expresiones
    public static void procesarOperador(char operador, Stack<Nodo> stackExpresion) {
        Nodo derecho = stackExpresion.pop();
        Nodo izquierdo = stackExpresion.pop();
        Nodo nuevoNodo = new Nodo(operador);
        nuevoNodo.derecho = derecho;
        nuevoNodo.izquierdo = izquierdo;
        stackExpresion.push(nuevoNodo);
    }

    // Método para verificar la precedencia de operadores
    public static boolean tienePrecedencia(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        } else {
            return true;
        }
    }
}