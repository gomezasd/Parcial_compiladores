# The Feline Graph Chronicles

Proyecto final del curso **Lenguajes y Compiladores** — Universidad EIA.

Sistema en Java con GUI (Swing) que resuelve las 4 misiones de grafos del
enunciado: BFS/DFS (Misión 1), Dijkstra (Misión 2), Floyd-Warshall y
Bellman-Ford (Misión 3), y Kruskal con Union-Find (Misión 4).

## Integrantes del grupo

- [Nombre 1]
- [Nombre 2]
- [Nombre 3]

## Requisitos

- JDK 17 o superior
- Maven (el proyecto usa `pom.xml`)

## Cómo compilar y ejecutar

Con Maven y el plugin `exec-maven-plugin` configurado en `pom.xml`:

```bash
mvn clean compile exec:java -Dexec.mainClass="com.eia.felinegraphchronicles.Main"
```

Si el `pom.xml` no tiene ese plugin, usa esta alternativa equivalente (dos
comandos, pero igual de reproducible desde un clon limpio):

```bash
mvn clean package
java -cp target/classes com.eia.felinegraphchronicles.Main
```

También puedes correr `com.eia.felinegraphchronicles.Main` directamente
desde cualquier IDE (IntelliJ, Eclipse) importando el proyecto como Maven.

## Cómo usar la aplicación

1. Al abrir, elige una de las 4 misiones desde el menú principal.
2. Pulsa **"Cargar ejemplo"** para llenar el input con el caso de muestra
   del enunciado, o pega/escribe tu propio input en el área de texto.
3. Pulsa **"Resolver"**. El resultado (`Case #k: ...`) aparece en el panel
   de texto, y la visualización del primer caso de prueba se dibuja al
   lado (grid, grafo o red, según la misión).
4. Usa **"← Volver al menú"** para cambiar de misión sin cerrar la
   aplicación.

## Estructura del proyecto

```
src/main/java/com/eia/felinegraphchronicles/
├── Main.java                      # Punto de entrada (arranca la GUI en el Event Dispatch Thread)
├── algorithms/                    # Algoritmos puros, sin ningún import de Swing/JavaFX
│   ├── mission1/
│   │   ├── BfsSolver.java         # BFS iterativo con cola (ArrayDeque)
│   │   └── DfsSolver.java         # DFS iterativo con pila explícita (no recursivo)
│   ├── mission2/
│   │   └── Dijkstra.java          # Dijkstra con PriorityQueue
│   ├── mission3/
│   │   ├── FloydWarshallSolver.java
│   │   ├── BellmanFordSolver.java
│   │   └── Mission3Orchestrator.java  # Corre ambos y valida que coincidan (cross-check)
│   └── mission4/
│       └── Kruskal.java           # Kruskal + Union-Find (compresión de caminos, unión por tamaño)
├── modelo/
│   ├── Grid.java                  # Cuadrícula de la Misión 1 (bombas)
│   ├── Graph.java                 # Grafo genérico (dirigido/no dirigido) de Misiones 2, 3 y 4
│   ├── Edge.java                  # Arista (from, to, weight)
│   └── resultado/
│       ├── PathResult.java        # Resultado de BFS/DFS (Misión 1)
│       └── MaxWalkResult.java     # Resultado de Misión 3 (OK / UNBOUNDED / UNREACHABLE)
├── io/
│   ├── InputTokenizer.java        # Envuelve Scanner para leer tokens sin importar el formato de líneas
│   ├── ParseException.java        # Errores de formato de input, siempre con mensaje legible
│   ├── Mission1Case.java / Mission1Parser.java
│   ├── Mission2Case.java / Mission2Parser.java
│   ├── Mission3Case.java / Mission3Parser.java
│   └── Mission4Case.java / Mission4Parser.java
├── util/
│   └── Sentinels.java             # Constantes centralizadas para "no hay ruta" / "inalcanzable"
├── visualizacion/
│   ├── GridRenderer.java          # Dibuja el grid de Misión 1 (límite: 50x50)
│   ├── GraphRenderer.java         # Dibuja el grafo de Misiones 2 y 3 (límite: 60 nodos)
│   ├── NetworkRenderer.java       # Dibuja la red de Misión 4 (límite: 100 nodos / 300 cables)
│   └── MatrixRenderer.java        # Tabla NxN con scroll para Floyd-Warshall (Misión 3)
└── gui/
    ├── MainFrame.java             # Menú principal (CardLayout entre misiones)
    ├── Mission1Panel.java
    ├── Mission2Panel.java
    ├── Mission3Panel.java
    └── Mission4Panel.java
```

## Decisiones de diseño

- **DFS iterativo, no en un Thread aparte**: se optó por simular la
  recursión con una pila explícita (`Deque` de frames `{fila, columna,
  próxima dirección}`) en vez de correr una DFS recursiva real en un hilo
  con stack ampliado. Con grids de hasta 10⁶ celdas, la pila explícita
  evita el `StackOverflowError` sin depender de configurar el tamaño de
  stack de un `Thread`.
- **Estructuras de datos**:
  - `PriorityQueue<long[]>` para Dijkstra, guardando `(distancia, nodo)`
    siempre como `long` (nunca `int`), porque con N hasta 10 000 y pesos
    hasta 1 000 000 la distancia acumulada puede superar el rango de
    `int`.
  - Lista de adyacencia (`Graph.adjacencyList()`) para Dijkstra y
    Bellman-Ford, en vez de recorrer todas las aristas en cada paso.
  - Union-Find con compresión de caminos y unión por tamaño para Kruskal,
    para que `find`/`union` sean casi O(1) amortizado.
  - Dos sentinelas distintos para "no hay ruta": `Sentinels.NO_ROUTE` /
    `Sentinels.UNREACHABLE` (muy negativo, pensado para la
    **maximización** de Misión 3) y una constante `INFINITY` propia
    dentro de `Dijkstra` (muy positiva, pensada para la **minimización**
    de Misión 2) — mezclar ambos rompe la condición de relajación.
- **Sin librerías de grafos**: todo el core algorítmico (BFS, DFS,
  Dijkstra, Floyd-Warshall, Bellman-Ford, Kruskal) está implementado
  desde cero, como exige el enunciado.
- **Sin librería externa de dibujo**: la visualización (grid, grafo,
  red, matriz) se dibuja a mano con `Graphics2D` de Swing (layout
  circular calculado manualmente para los grafos), sin JGraphT,
  GraphStream ni ninguna librería de layout.
- **Cross-check de Misión 3**: `Mission3Orchestrator` corre
  Floyd-Warshall y Bellman-Ford para cada caso y lanza una excepción si
  no coinciden en estado o en valor, en vez de mostrar en silencio un
  resultado que podría ser incorrecto.

## Limitaciones conocidas

- La visualización solo dibuja el **primer** caso de prueba de cada
  input (dibujar todos los casos a la vez no sería legible); el texto
  de salida sí incluye todos los casos.
- Por encima de los umbrales de la Sección 2.3 (50×50 en Misión 1, 60
  nodos en Misiones 2/3, 100 nodos / 300 cables en Misión 4), el dibujo
  se omite y se muestra un mensaje explicando por qué, pero el resultado
  numérico se sigue calculando y mostrando normalmente.
- En Misión 4, si el input contiene dos cables idénticos (mismo par de
  nodos y mismo costo) y Kruskal solo usa uno para el MST, la
  visualización podría resaltar ambos como "usados" en un caso muy de
  borde, porque la comparación se hace por valor.

## Pruebas automatizadas

Cada algoritmo tiene al menos una prueba automatizada usando los
ejemplos de este enunciado como valores esperados (ver `src/test`).
