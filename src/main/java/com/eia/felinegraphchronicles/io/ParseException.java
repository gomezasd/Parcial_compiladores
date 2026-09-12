package com.eia.felinegraphchronicles.io;

/**
 * Se lanza cuando el input pegado en la GUI no tiene el formato esperado.
 * La GUI debe atrapar esta excepción y mostrar getMessage() en pantalla,
 * NUNCA dejar que se propague como un stack trace (exigencia de la
 * Sección 2.2 del enunciado).
 */
public class ParseException extends RuntimeException {
    public ParseException(String message) {
        super(message);
    }
}
