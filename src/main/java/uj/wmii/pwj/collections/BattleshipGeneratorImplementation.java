package uj.wmii.pwj.collections;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class BattleshipGeneratorImplementation implements BattleshipGenerator{

    static public class Cell {
        public final int row;
        public final int col;
        public boolean occupied;
        public boolean available;

        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
            this.occupied = false;
            this.available = true;
        }
    }

    static public class GenerationException extends ExecutionException {}

    private final Cell[][] map;
    private List<Cell> availableCells;

    public Cell getRandomCellFromList(List<Cell> cells) throws GenerationException{
        int numOfCells = cells.size();
        if (numOfCells==0) throw new GenerationException();

        Random random = new Random();
        int randomCellId = random.nextInt(numOfCells);

        Cell randomCell =  cells.get(randomCellId);
        randomCell.occupied = true;
        randomCell.available = false;

        return randomCell;
    }

    public boolean inMap(int row, int col) {
        if (row<0 || row>9) return false;
        return col >= 0 && col <= 9;
    }

    public void disableNeighbours(Cell center) {
        int row = center.row-1;
        int col = center.col-1;

        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                if (inMap(row+i, col+j)) {
                    Cell disabled = map[row+i][col+j];
                    disabled.available = false;
                    availableCells.remove(disabled);
                }
            }
        }

    }

    public List<Cell> getAvailableNeighbours(Cell center) {
        int row = center.row;
        int col = center.col;
        List<Cell> neighbours = new LinkedList<>();

        if (row>0 && map[row-1][col].available) neighbours.add(map[row-1][col]);
        if (row<9 && map[row+1][col].available) neighbours.add(map[row+1][col]);
        if (col>0 && map[row][col-1].available) neighbours.add(map[row][col-1]);
        if (col<9 && map[row][col+1].available) neighbours.add(map[row][col+1]);

        return neighbours;
    }

    void generateMasts(int n) throws GenerationException{
        Cell[] masts = new Cell[n];
        List<Cell> availableNeighbours;
        List<Cell> availableNeighboursToAdd;
        Set<Cell> merged;

        Cell firstMast = getRandomCellFromList(this.availableCells);
        availableNeighbours = getAvailableNeighbours(firstMast);
        masts[0] = firstMast;

        for (int i=1; i<n-1; i++) {
            masts[i] = getRandomCellFromList(availableNeighbours);
            availableNeighbours.remove(masts[i]);
            availableNeighboursToAdd = getAvailableNeighbours(masts[i]);

            merged = new LinkedHashSet<>();
            merged.addAll(availableNeighbours);
            merged.addAll(availableNeighboursToAdd);
            availableNeighbours = new LinkedList<>(merged);
        }

        if (n>1) {
            Cell lastMast = getRandomCellFromList(availableNeighbours);
            masts[n - 1] = lastMast;
        }

        for (Cell mast : masts) disableNeighbours(mast);
    }

    public BattleshipGeneratorImplementation() {
        this.map = new Cell[10][10];
        boolean succesfullGeneration = false;

        while (!succesfullGeneration) {
            this.availableCells = new LinkedList<>();
            succesfullGeneration = true;

            for (int row = 0; row < 10; row++) {
                for (int col = 0; col < 10; col++) {
                    Cell cell = new Cell(row, col);
                    map[row][col] = cell;
                    availableCells.add(cell);
                }
            }

            try {
                generateMasts(4);
                for (int i=0; i<2; i++) generateMasts(3);
                for (int i=0; i<3; i++) generateMasts(2);
                for (int i=0; i<4; i++) generateMasts(1);
            } catch (GenerationException e) {
                succesfullGeneration = false;
            }
        }
    }

    @Override
    public String generateMap() {
        char[] charMap = new char[100];

        for (int i=0; i<10; i++) {
            for (int j=0; j<10; j++) {
                Cell curr = map[i][j];
                if (!curr.occupied) {
                    charMap[10*i+j] = '.';
                }
                else {
                    charMap[10*i+j] = '#';
                }
            }
        }

        return new String(charMap);
    }
}