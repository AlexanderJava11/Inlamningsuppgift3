import java.util.Random;
import java.util.Stack;

// Klassen Board representerar spelbrädet för 15-spelet.
// Den innehåller rutnätet av Tile-objekt och all logik för att flytta,
// blanda, ångra/göra om och kontrollera om brädet är löst.
public final class Board {
    // Storleken på brädet (t.ex. 4 för 4x4).
    private final int SIZE;

    // Tvådimensionell array som håller alla rutor (Tiles).
    private Tile[][] grid;

    // Positionen för den tomma rutan (rad).
    private int emptyRow;

    // Positionen för den tomma rutan (kolumn).
    private int emptyCol;

    // Antal drag som gjorts i aktuellt spel.
    private int moves;

    // Stack för att lagra tidigare drag (för ångra).
    private final Stack<Move> undoStack = new Stack();

    // Stack för att lagra ångrade drag (för gör om).
    private final Stack<Move> redoStack = new Stack();

    // Konstruktor som skapar ett bräde med given storlek.
    // Om storleken är mindre än 2 sätts den till 2.
    public Board(int size) {
        if (size < 2) {
            size = 2;
        }

        this.SIZE = size;
        this.initSolved(); // Initiera brädet i löst (målkonfiguration).
    }

    // initSolved() fyller brädet med värden i ordning och sätter
    // sista rutan som tom (value 0). Nollställer moves och stackar.
    public void initSolved() {
        this.grid = new Tile[this.SIZE][this.SIZE];
        int value = 1;

        for (int row = 0; row < this.SIZE; ++row) {
            for (int col = 0; col < this.SIZE; ++col) {
                // Sista positionen är den tomma rutan.
                if (row == this.SIZE - 1 && col == this.SIZE - 1) {
                    this.grid[row][col] = new Tile(0);
                    this.emptyRow = row;
                    this.emptyCol = col;
                } else {
                    // Övriga rutor får värden 1..(SIZE*SIZE-1).
                    this.grid[row][col] = new Tile(value++);
                }
            }
        }

        // Nollställ antal drag och rensa ångra/gör om-stackarna.
        this.moves = 0;
        this.undoStack.clear();
        this.redoStack.clear();
    }

    // swap() byter plats på två rutor i grid.
    private void swap(int r1, int c1, int r2, int c2) {
        Tile temp = this.grid[r1][c1];
        this.grid[r1][c1] = this.grid[r2][c2];
        this.grid[r2][c2] = temp;
    }

    // canMove() returnerar true om rutan på (row,col) ligger intill den tomma rutan.
    // Endast rutor med Manhattan-distans 1 kan flyttas.
    public boolean canMove(int row, int col) {
        int dr = Math.abs(row - this.emptyRow);
        int dc = Math.abs(col - this.emptyCol);
        return dr + dc == 1;
    }

    // move() försöker flytta rutan på (row,col) till den tomma positionen.
    // Om flytt är giltig görs bytet, ett Move-objekt pushas på undoStack, redoStack rensas,
    // moves ökas och metoden returnerar true. Annars returneras false.
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

    // undo() ångrar senaste draget om möjligt.
    // Flyttar tillbaka brickan, uppdaterar tom-ruta, pushar draget till redoStack
    // och minskar moves. Returnerar true om ångran lyckades.
    public boolean undo() {
        if (this.undoStack.isEmpty()) {
            return false;
        } else {
            Move move = (Move) this.undoStack.pop();
            this.swap(move.toRow, move.toCol, move.fromRow, move.fromCol);
            this.emptyRow = move.toRow;
            this.emptyCol = move.toCol;
            this.redoStack.push(move);
            this.moves = Math.max(0, this.moves - 1);
            return true;
        }
    }

    // redo() gör om ett tidigare ångrat drag om möjligt.
    // Flyttar brickan, uppdaterar tom-ruta, pushar draget tillbaka till undoStack
    // och ökar moves. Returnerar true om gör om lyckades.
    public boolean redo() {
        if (this.redoStack.isEmpty()) {
            return false;
        } else {
            Move move = (Move) this.redoStack.pop();
            this.swap(move.fromRow, move.fromCol, move.toRow, move.toCol);
            this.emptyRow = move.fromRow;
            this.emptyCol = move.fromCol;
            this.undoStack.push(move);
            ++this.moves;
            return true;
        }
    }

    // canUndo() returnerar true om det finns något drag att ångra.
    public boolean canUndo() {
        return !this.undoStack.isEmpty();
    }

    // canRedo() returnerar true om det finns något drag att göra om.
    public boolean canRedo() {
        return !this.redoStack.isEmpty();
    }

    // shuffle() blandar brädet genom att göra ett antal slumpmässiga giltiga drag.
    // Tar antal steg och en Random-instans som parameter.
    public void shuffle(int steps, Random rand) {
        for (int i = 0; i < steps; ++i) {
            // Lista potentiella grannar runt den tomma rutan.
            int[][] neigh = new int[][]{
                    {this.emptyRow - 1, this.emptyCol},
                    {this.emptyRow + 1, this.emptyCol},
                    {this.emptyRow, this.emptyCol - 1},
                    {this.emptyRow, this.emptyCol + 1}
            };
            int[] valid = new int[4];
            int count = 0;

            // Filtrera bort grannar som ligger utanför brädet.
            for (int j = 0; j < neigh.length; ++j) {
                int rr = neigh[j][0];
                int cc = neigh[j][1];
                if (rr >= 0 && rr < this.SIZE && cc >= 0 && cc < this.SIZE) {
                    valid[count++] = j;
                }
            }

            // Välj en av de giltiga grannarna slumpmässigt och byt plats med den.
            int pick = valid[rand.nextInt(count)];
            int nr = neigh[pick][0];
            int nc = neigh[pick][1];
            this.swap(this.emptyRow, this.emptyCol, nr, nc);
            this.emptyRow = nr;
            this.emptyCol = nc;
        }

        // Efter blandning nollställs moves och stackarna rensas.
        this.moves = 0;
        this.undoStack.clear();
        this.redoStack.clear();
    }

    // isSolved() kontrollerar om brädet är i målläge (1..N-1 i ordning och tom ruta sist).
    public boolean isSolved() {
        int expected = 1;

        for (int row = 0; row < this.SIZE; ++row) {
            for (int col = 0; col < this.SIZE; ++col) {
                // Sista rutan ska vara tom.
                if (row == this.SIZE - 1 && col == this.SIZE - 1) {
                    if (!this.grid[row][col].isEmpty()) {
                        return false;
                    }
                } else if (this.grid[row][col].value() != expected) {
                    // Övriga rutor ska ha förväntat värde.
                    return false;
                }

                ++expected;
            }
        }

        return true;
    }

    // getMoves() returnerar antal drag som gjorts.
    public int getMoves() {
        return this.moves;
    }
}