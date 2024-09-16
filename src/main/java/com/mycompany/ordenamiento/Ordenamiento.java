package com.mycompany.ordenamiento;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class Ordenamiento {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Compilador compilador = new Compilador();
        Automata automata = new Automata();

        System.out.print("Ingrese el fragmento de código: ");
        String fragmentoCodigo = scanner.nextLine();

        // Verificar que el código empiece con '<' y termine con '>'
        if (!fragmentoCodigo.startsWith("<") || !fragmentoCodigo.endsWith(">")) {
            System.out.println("Error: El código debe empezar con '<' y terminar con '>'");
            return;
        }

        // Remover los símbolos '<' y '>' para obtener solo la expresión matemática
        String expresion = fragmentoCodigo.substring(1, fragmentoCodigo.length() - 1);

        // Realizar análisis léxico
        System.out.println("Realizando análisis léxico...");
        compilador.analizar(expresion);
        compilador.imprimirTokens();

        // Analizar con el autómata
        System.out.println("Analizando con el autómata...");
        analizarConAutomata(expresion, automata);

        // Mapa para almacenar los valores de las variables
        Map<Character, Integer> variables = new HashMap<>();

        // Pedir valores para las variables
        for (char c : expresion.toCharArray()) {
            if (Character.isAlphabetic(c) && !variables.containsKey(c)) {
                System.out.print("Ingrese el valor para " + c + ": ");
                int valor = scanner.nextInt();
                variables.put(c, valor);
            }
        }

        // Mostrar la expresión matemática con los valores ingresados
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

        // Evaluar la expresión y mostrar el resultado
        int resultado = evaluar(expresion, variables);
        System.out.println("El resultado de la expresion es: " + resultado);

        scanner.close();
    }

    // Método para analizar el fragmento de código con el autómata
    public static void analizarConAutomata(String expresion, Automata automata) {
        for (char c : expresion.toCharArray()) {
            Automata.Simbolo simbolo;
            if (Character.isAlphabetic(c)) {
                simbolo = Automata.Simbolo.VARIABLE;
            } else if (c == '=') {
                simbolo = Automata.Simbolo.ASIGNACION;
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                simbolo = Automata.Simbolo.OPERADOR;
            } else if (Character.isDigit(c)) {
                simbolo = Automata.Simbolo.NUMERO;
            } else if (c == '(' || c == ')') {
                simbolo = Automata.Simbolo.PARENTESIS;
            } else {
                simbolo = Automata.Simbolo.DESCONOCIDO;
            }
            automata.procesarSimbolo(simbolo);
        }

        if (automata.esEstadoFinal()) {
            System.out.println("El código es aceptado por el autómata.");
        } else {
            System.out.println("El código no es aceptado por el autómata.");
        }
    }

    // Método para evaluar la expresión matemática y calcular su resultado
    public static int evaluar(String expresion, Map<Character, Integer> variables) {
        // Construir el árbol de expresiones
        Arbol arbol = new Arbol();
        arbol.raiz = construirArbol(expresion, variables);

        // Evaluar el árbol de expresiones
        return evaluarArbol(arbol.raiz);
    }

    // Método para evaluar el árbol de expresiones recursivamente
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

    // Método para construir el árbol de expresiones
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

        // El resultado final estará en la raíz del árbol de expresiones
        return stackExpresion.pop();
    }

    // Método para procesar operadores y construir el árbol de expresiones
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
