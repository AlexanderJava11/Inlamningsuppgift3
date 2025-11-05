import java.util.Random;
import java.util.Stack;

public final class Board {
    private final int SIZE;
    private Tile[][] grid;
    private int emptyRow;
    private int emptyCol;
    private int moves;
    private final Stack<Move> undoStack = new Stack();
    private final Stack<Move> redoStack = new Stack();

    public Board(int size) {
        if (size < 2) {
            size = 2;
        }

        this.SIZE = size;
        this.initSolved();
    }

    public void initSolved() {
        this.grid = new Tile[this.SIZE][this.SIZE];
        int value = 1;

        for(int row = 0; row < this.SIZE; ++row) {
            for(int col = 0; col < this.SIZE; ++col) {
                if (row == this.SIZE - 1 && col == this.SIZE - 1) {
                    this.grid[row][col] = new Tile(0);
                    this.emptyRow = row;
                    this.emptyCol = col;
                } else {
                    this.grid[row][col] = new Tile(value++);
                }
            }
        }

        this.moves = 0;
        this.undoStack.clear();
        this.redoStack.clear();
    }

    private void swap(int r1, int c1, int r2, int c2) {
        Tile temp = this.grid[r1][c1];
        this.grid[r1][c1] = this.grid[r2][c2];
        this.grid[r2][c2] = temp;
    }

    public boolean canMove(int row, int col) {
        int dr = Math.abs(row - this.emptyRow);
        int dc = Math.abs(col - this.emptyCol);
        return dr + dc == 1;
    }

    public boolean move(int row, int col) {
        if (!this.canMove(row, col)) {
            return false;
        } else {
            Move m = new Move(row, col, this.emptyRow, this.emptyCol);
            this.swap(row, col, this.emptyRow, this.emptyCol);
            this.emptyRow = row;
            this.emptyCol = col;
            this.undoStack.push(m);
            this.redoStack.clear();
            ++this.moves;
            return true;
        }
    }

    public boolean undo() {
        if (this.undoStack.isEmpty()) {
            return false;
        } else {
            Move move = (Move)this.undoStack.pop();
            this.swap(move.toRow, move.toCol, move.fromRow, move.fromCol);
            this.emptyRow = move.toRow;
            this.emptyCol = move.toCol;
            this.redoStack.push(move);
            this.moves = Math.max(0, this.moves - 1);
            return true;
        }
    }

    public boolean redo() {
        if (this.redoStack.isEmpty()) {
            return false;
        } else {
            Move move = (Move)this.redoStack.pop();
            this.swap(move.fromRow, move.fromCol, move.toRow, move.toCol);
            this.emptyRow = move.fromRow;
            this.emptyCol = move.fromCol;
            this.undoStack.push(move);
            ++this.moves;
            return true;
        }
    }

    public boolean canUndo() {
        return !this.undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !this.redoStack.isEmpty();
    }

    public void shuffle(int steps, Random rand) {
        for(int i = 0; i < steps; ++i) {
            int[][] neigh = new int[][]{{this.emptyRow - 1, this.emptyCol}, {this.emptyRow + 1, this.emptyCol}, {this.emptyRow, this.emptyCol - 1}, {this.emptyRow, this.emptyCol + 1}};
            int[] valid = new int[4];
            int count = 0;

            for(int j = 0; j < neigh.length; ++j) {
                int rr = neigh[j][0];
                int cc = neigh[j][1];
                if (rr >= 0 && rr < this.SIZE && cc >= 0 && cc < this.SIZE) {
                    valid[count++] = j;
                }
            }

            int pick = valid[rand.nextInt(count)];
            int nr = neigh[pick][0];
            int nc = neigh[pick][1];
            this.swap(this.emptyRow, this.emptyCol, nr, nc);
            this.emptyRow = nr;
            this.emptyCol = nc;
        }

        this.moves = 0;
        this.undoStack.clear();
        this.redoStack.clear();
    }

    public boolean isSolved() {
        int expected = 1;

        for(int row = 0; row < this.SIZE; ++row) {
            for(int col = 0; col < this.SIZE; ++col) {
                if (row == this.SIZE - 1 && col == this.SIZE - 1) {
                    if (!this.grid[row][col].isEmpty()) {
                        return false;
                    }
                } else if (this.grid[row][col].value() != expected) {
                    return false;
                }

                ++expected;
            }
        }

        return true;
    }

    public int getMoves() {
        return this.moves;
    }

    public Tile get(int row, int col) {
        return this.grid[row][col];
    }

    public int size() {
        return this.SIZE;
    }
}
