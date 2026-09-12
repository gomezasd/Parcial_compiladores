package com.eia.felinegraphchronicles.algorithms.mission1;

import com.eia.felinegraphchronicles.modelo.Grid;
import com.eia.felinegraphchronicles.modelo.resultado.PathResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Mission1SampleTest {

    private Grid buildSampleGrid() {
        Grid grid = new Grid(10, 10);
        grid.markBomb(0, 2);
        grid.markBomb(1, 2);
        grid.markBomb(2, 2);
        grid.markBomb(2, 9);
        grid.markBomb(3, 1);
        grid.markBomb(3, 7);
        grid.markBomb(5, 3);
        grid.markBomb(5, 6);
        grid.markBomb(5, 9);
        grid.markBomb(6, 0);
        grid.markBomb(6, 1);
        grid.markBomb(6, 2);
        grid.markBomb(6, 7);
        grid.markBomb(7, 0);
        grid.markBomb(7, 3);
        grid.markBomb(7, 8);
        grid.markBomb(8, 7);
        grid.markBomb(8, 9);
        grid.markBomb(9, 2);
        grid.markBomb(9, 3);
        grid.markBomb(9, 4);
        return grid;
    }

    @Test
    void bfsEncuentraElCaminoMasCorto() {
        Grid grid = buildSampleGrid();
        PathResult result = BfsSolver.solve(grid, 0, 0, 9, 9);

        assertTrue(result.isReachable());
        assertEquals(18, result.moves());
    }

    @Test
    void dfsEncuentraUnCaminoValidoAunqueNoOptimo() {
        Grid grid = buildSampleGrid();
        PathResult result = DfsSolver.solve(grid, 0, 0, 9, 9);

        assertTrue(result.isReachable());
        assertEquals(32, result.moves());
    }

    @Test
    void siInicioEsIgualADestinoLaRespuestaEsCero() {
        Grid grid = buildSampleGrid();
        PathResult result = BfsSolver.solve(grid, 0, 0, 0, 0);

        assertTrue(result.isReachable());
        assertEquals(0, result.moves());
    }

    @Test
    void siElInicioTieneBombaEsInalcanzable() {
        Grid grid = buildSampleGrid();
        PathResult result = BfsSolver.solve(grid, 0, 2, 9, 9);

        assertTrue(!result.isReachable());
    }
}