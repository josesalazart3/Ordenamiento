
package com.mycompany.ordenamiento;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compilador {

    // Definir tipos de tokens con expresiones regulares
    private static final String IDENTIFICADOR = "[a-zA-Z_][a-zA-Z0-9_]*";
    private static final String NUMERO = "\\d+";
    private static final String OPERADOR = "[+\\-*/=]";
    private static final String PARENTESIS = "[()]";
    private static final String ESPACIO = "\\s+";

    // Lista de tokens reconocidos
    private List<Token> tokens;

    public Compilador() {
        tokens = new ArrayList<>();
    }

    // Método de análisis léxico
    public void analizar(String codigo) {
        tokens.clear(); // Limpiar lista de tokens previos

        // Unir todos los patrones de tokens en una sola expresión regular
        String patron = String.format("(%s)|(%s)|(%s)|(%s)|(%s)",
                IDENTIFICADOR, NUMERO, OPERADOR, PARENTESIS, ESPACIO);

        // Compilar el patrón y aplicarlo sobre la cadena de entrada
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(codigo);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                tokens.add(new Token("IDENTIFICADOR", matcher.group(1)));
            } else if (matcher.group(2) != null) {
                tokens.add(new Token("NUMERO", matcher.group(2)));
            } else if (matcher.group(3) != null) {
                tokens.add(new Token("OPERADOR", matcher.group(3)));
            } else if (matcher.group(4) != null) {
                tokens.add(new Token("PARENTESIS", matcher.group(4)));
            } else if (matcher.group(5) != null) {
                // Ignorar los espacios en blanco
            }
        }
    }

    // Método para imprimir los tokens obtenidos
    public void imprimirTokens() {
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    // Clase interna para representar un token
    private class Token {
        private String tipo;
        private String valor;

        public Token(String tipo, String valor) {
            this.tipo = tipo;
            this.valor = valor;
        }

        @Override
        public String toString() {
            return String.format("Token(%s, '%s')", tipo, valor);
        }
    }

    // Método para validar si una cadena es aceptada por el lenguaje utilizando autómata finito
    public boolean esCadenaAceptada(String cadena) {
        // Aquí definimos el autómata para validar un patrón de cadenas aceptadas
        Pattern patron = Pattern.compile(IDENTIFICADOR); // Ejemplo de autómata que acepta identificadores
        Matcher matcher = patron.matcher(cadena);
        return matcher.matches();
    }

}

