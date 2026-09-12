package com.eia.felinegraphchronicles.util;

/**
 * Constantes centralizadas para representar "casos especiales", como
 * "no existe ruta" o "no alcanzable". El enunciado advierte
 * explícitamente: NUNCA hacer sumas/restas directamente sobre estos
 * valores centinela sin antes comprobar si un número ES un centinela,
 * porque se generarían resultados sin sentido (o desbordamientos).
 */
public final class Sentinels {

    private Sentinels() {
        // clase de solo constantes, no se instancia
    }

    // Usamos MIN_VALUE / 2 (no MIN_VALUE directo) para dejar "margen":
    // así, aunque sumemos dos centinelas por accidente, no se desborda
    // el rango de long (evita bugs silenciosos).
    public static final long NO_ROUTE = Long.MIN_VALUE / 2;
    public static final long UNREACHABLE = Long.MIN_VALUE / 2;

    public static boolean isNoRoute(long value) {
        return value <= NO_ROUTE;
    }
}
