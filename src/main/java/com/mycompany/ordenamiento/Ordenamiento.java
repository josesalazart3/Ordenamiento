package com.mycompany.ordenamiento;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.Deque;
import java.util.ArrayDeque;


public class Ordenamiento {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Compilador compilador = new Compilador();
        
        List<String> expresiones = new ArrayList<>();
        
        System.out.println("Ingrese varias expresiones matemáticas (cada una en una línea). Escriba 'FIN' para terminar:");
        
        // Leer varias expresiones hasta que se ingrese 'FIN'
        while (true) {
            String expresion = scanner.nextLine();
            if (expresion.equalsIgnoreCase("FIN")) {
                break;
            }
            expresiones.add(expresion);
        }
        
        for (int i = 0; i < expresiones.size(); i++) {
            String fragmentoCodigo = expresiones.get(i);
            System.out.println("\nProcesando Expresión " + (i + 1) + ":");
            
            // Verificar que el código empiece con '<' y termine con '>'
            if (!fragmentoCodigo.startsWith("<") || !fragmentoCodigo.endsWith(">")) {
                System.out.println("Error: El código debe empezar con '<' y terminar con '>'");
                continue;
            }
            
            // Remover los símbolos '<' y '>' para obtener solo la expresión matemática
            String expresion = fragmentoCodigo.substring(1, fragmentoCodigo.length() - 1);
            
            // Realizar análisis léxico
            System.out.println("Realizando análisis léxico...");
            compilador.analizar(expresion);
            compilador.imprimirTokens();
            
            // Analizar con el autómata
            System.out.println("Analizando con el autómata...");
            Automata automata = new Automata(); // Reiniciar el autómata para cada expresión
            analizarConAutomata(expresion, automata);
            
            // Continuar si el autómata acepta la expresión
            if (!automata.esEstadoFinal()) {
                continue;
            }
            
            // Mapa para almacenar los valores de las variables
            Map<Character, Integer> variables = new HashMap<>();
            
            // Pedir valores para las variables
            for (char c : expresion.toCharArray()) {
                if (Character.isAlphabetic(c) && !variables.containsKey(c)) {
                    System.out.print("Ingrese el valor para la variable " + c + ": ");
                    int valor = scanner.nextInt();
                    variables.put(c, valor);
                }
            }
            
            // Mostrar la expresión matemática con los valores ingresados
            System.out.println("Expresión con valores ingresados:");
            String expresionConValores = reemplazarValores(expresion, variables);
            System.out.println(expresionConValores);
            
            // Evaluar la expresión y mostrar el resultado
            int resultado = evaluar(expresionConValores);
            System.out.println("El resultado de Expresión " + (i + 1) + " es: " + resultado);
        }

        scanner.close();
    }

    // Método para analizar el fragmento de código con el autómata
    public static void analizarConAutomata(String expresion, Automata automata) {
        for (char c : expresion.toCharArray()) {
            Automata.Simbolo simbolo = obtenerSimbolo(c);
            automata.procesarSimbolo(simbolo);
        }

        if (automata.esEstadoFinal()) {
            System.out.println("El código es aceptado por el autómata.");
        } else {
            System.out.println("El código no es aceptado por el autómata.");
        }
    }

    // Método para obtener el tipo de símbolo
    public static Automata.Simbolo obtenerSimbolo(char c) {
        if (Character.isAlphabetic(c)) {
            return Automata.Simbolo.VARIABLE;
        } else if (c == '=') {
            return Automata.Simbolo.ASIGNACION;
        } else if ("+-*/".indexOf(c) != -1) {
            return Automata.Simbolo.OPERADOR;
        } else if (Character.isDigit(c)) {
            return Automata.Simbolo.NUMERO;
        } else if (c == '(' || c == ')') {
            return Automata.Simbolo.PARENTESIS;
        } else {
            return Automata.Simbolo.DESCONOCIDO;
        }
    }

    // Método para reemplazar las variables en la expresión por sus valores
    public static String reemplazarValores(String expresion, Map<Character, Integer> variables) {
        StringBuilder expresionConValores = new StringBuilder();
        for (char c : expresion.toCharArray()) {
            if (Character.isAlphabetic(c)) {
                expresionConValores.append(variables.get(c));
            } else {
                expresionConValores.append(c);
            }
        }
        return expresionConValores.toString();
    }

    // Método para evaluar la expresión matemática
    public static int evaluar(String expresion) {
        Deque<Integer> operandos = new ArrayDeque<>();
        Deque<Character> operadores = new ArrayDeque<>();

        char[] chars = expresion.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ') continue;

            if (Character.isDigit(chars[i])) {
                int valor = 0;
                while (i < chars.length && Character.isDigit(chars[i])) {
                    valor = valor * 10 + (chars[i++] - '0');
                }
                i--; // Retroceder para procesar el siguiente caracter correctamente
                operandos.push(valor);
            } else if (chars[i] == '(') {
                operadores.push(chars[i]);
            } else if (chars[i] == ')') {
                while (operadores.peek() != '(') {
                    operandos.push(procesarOperador(operadores.pop(), operandos.pop(), operandos.pop()));
                }
                operadores.pop(); // Remover el '('
            } else if ("+-*/".indexOf(chars[i]) != -1) {
                while (!operadores.isEmpty() && tienePrecedencia(chars[i], operadores.peek())) {
                    operandos.push(procesarOperador(operadores.pop(), operandos.pop(), operandos.pop()));
                }
                operadores.push(chars[i]);
            }
        }

        // Procesar los operadores restantes
        while (!operadores.isEmpty()) {
            operandos.push(procesarOperador(operadores.pop(), operandos.pop(), operandos.pop()));
        }

        return operandos.pop(); // El resultado final
    }

    // Método para procesar un operador
    public static int procesarOperador(char operador, int derecho, int izquierdo) {
        switch (operador) {
            case '+': return izquierdo + derecho;
            case '-': return izquierdo - derecho;
            case '*': return izquierdo * derecho;
            case '/': 
                if (derecho == 0) throw new ArithmeticException("División por cero");
                return izquierdo / derecho;
            default: return 0;
        }
    }

    // Método para verificar la precedencia de operadores
    public static boolean tienePrecedencia(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        return (op1 != '*' && op1 != '/') || (op2 == '+' || op2 == '-');
    }
}
