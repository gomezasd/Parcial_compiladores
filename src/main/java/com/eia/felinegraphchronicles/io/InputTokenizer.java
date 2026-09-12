package com.eia.felinegraphchronicles.io;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Envuelve un Scanner para leer el input como un STREAM de tokens
 * separados por espacios en blanco (espacios, tabs, saltos de línea),
 * sin importar cuántos tokens haya por renglón ni si hay líneas en
 * blanco de más — tal como exige la Sección 2.2 del enunciado.
 *
 * Cualquier error de lectura (token faltante, o texto donde se
 * esperaba un número) se traduce a un ParseException con mensaje
 * legible, en vez de dejar que explote una excepción críptica de Java.
 */
public final class InputTokenizer {

    private final Scanner scanner;

    public InputTokenizer(String rawInput) {
        // Scanner, por defecto, ya separa tokens por CUALQUIER espacio en
        // blanco (space, tab, \n, \r) y SALTA automáticamente líneas
        // vacías o espacios repetidos — es justo el comportamiento que
        // pide el enunciado, sin necesidad de parsear línea por línea.
        this.scanner = new Scanner(rawInput);
    }

    public boolean hasNextInt() {
        return scanner.hasNextInt();
    }

    public int nextInt() {
        try {
            return scanner.nextInt();
        } catch (NoSuchElementException | IllegalStateException e) {
            throw new ParseException("Se esperaba un número entero y no se encontró (input incompleto o mal formado)");
        }
    }

    public long nextLong() {
        try {
            return scanner.nextLong();
        } catch (NoSuchElementException | IllegalStateException e) {
            throw new ParseException("Se esperaba un número (long) y no se encontró (input incompleto o mal formado)");
        }
    }
}
