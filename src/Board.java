import java.util.Random;   // Importerar klassen Random för att kunna slumpa drag
import java.util.Stack;    // Importerar Stack-klassen för att kunna spara tidigare drag (undo/gör om)

// Klassen Board är själva spelbrädet för 15-spelet
public final class Board {

    private final int SIZE;        // Antalet rader och kolumner på brädet (t.ex. 4 för 4x4)
    private Tile[][] grid;         // Tvådimensionell array som innehåller alla brickor (Tiles)
    private int emptyRow;          // Radpositionen för den tomma rutan
    private int emptyCol;          // Kolumnpositionen för den tomma rutan
    private int moves;             // Räknare för hur många drag som gjorts i spelet

    private final Stack<Move> undoStack = new Stack<>();  // Stack som sparar gjorda drag för att kunna ångra
    private final Stack<Move> redoStack = new Stack<>();  // Stack som sparar ångrade drag för att kunna göra om

    // Konstruktor – skapar ett nytt spelbräde med den storlek man anger
    public Board(int size) {
        if (size < 2) {       // Om storleken är mindre än 2, sätt den till 2 (minsta möjliga)
            size = 2;
        }

        this.SIZE = size;     // Sätter brädets storlek
        this.initSolved();    // Startar spelet med brädet i "löst" läge (1–15 i ordning)
    }

    // initSolved – gör ett färdigt bräde i ordning, med sista rutan tom
    public void initSolved() {
        this.grid = new Tile[this.SIZE][this.SIZE]; // Skapar en ny 2D-array med Tiles
        int value = 1;                              // Startvärde för numreringen av brickorna

        // Loopar igenom varje rad i brädet
        for (int row = 0; row < this.SIZE; ++row) {
            // Loopar igenom varje kolumn i raden
            for (int col = 0; col < this.SIZE; ++col) {

                // Om det är den sista rutan på sista raden → den ska vara tom (0)
                if (row == this.SIZE - 1 && col == this.SIZE - 1) {
                    this.grid[row][col] = new Tile(0);   // Skapa en tom Tile med värde 0
                    this.emptyRow = row;                 // Spara positionen för den tomma rutan (rad)
                    this.emptyCol = col;                 // Spara positionen för den tomma rutan (kolumn)
                } else {
                    // Annars skapa en Tile med nästa nummer
                    this.grid[row][col] = new Tile(value++);
                }
            }
        }

        this.moves = 0;           // Nollställer antalet drag
        this.undoStack.clear();   // Tömmer stacken för ångra
        this.redoStack.clear();   // Tömmer stacken för gör om
    }

    // swap – byter plats på två brickor i brädet
    private void swap(int r1, int c1, int r2, int c2) {
        Tile temp = this.grid[r1][c1];      // Sparar första brickan tillfälligt
        this.grid[r1][c1] = this.grid[r2][c2]; // Flyttar andra brickan till första platsen
        this.grid[r2][c2] = temp;           // Flyttar tillbaka den sparade brickan till andra platsen
    }

    // canMove – kollar om brickan kan flyttas (om den ligger bredvid den tomma rutan)
    public boolean canMove(int row, int col) {
        int dr = Math.abs(row - this.emptyRow); // Skillnad i rad
        int dc = Math.abs(col - this.emptyCol); // Skillnad i kolumn
        return dr + dc == 1;                    // Om de ligger exakt 1 steg ifrån varandra (sida vid sida)
    }

    // move – försöker flytta brickan, returnerar true om det gick
    public boolean move(int row, int col) {
        if (!this.canMove(row, col)) {      // Om flytten inte är giltig
            return false;                   // Gör inget och returnera false
        } else {
            Move m = new Move(row, col, this.emptyRow, this.emptyCol); // Skapar ett Move-objekt med info om flytten
            this.swap(row, col, this.emptyRow, this.emptyCol);          // Byter plats mellan brickan och tomrutan
            this.emptyRow = row;             // Uppdaterar tomrutans rad
            this.emptyCol = col;             // Uppdaterar tomrutans kolumn
            this.undoStack.push(m);          // Lägger flytten i undoStack (så vi kan ångra)
            this.redoStack.clear();          // Tömmer redoStack (eftersom vi gjort ett nytt drag)
            ++this.moves;                    // Ökar drag-räknaren med 1
            return true;                     // Flytten lyckades
        }
    }

    // undo – ångrar senaste draget om det går
    public boolean undo() {
        if (this.undoStack.isEmpty()) {      // Om det inte finns något att ångra
            return false;                    // Gör inget
        } else {
            Move move = this.undoStack.pop(); // Tar bort senaste draget från stacken
            this.swap(move.toRow, move.toCol, move.fromRow, move.fromCol); // Byter tillbaka brickan
            this.emptyRow = move.toRow;      // Uppdaterar tomrutans rad
            this.emptyCol = move.toCol;      // Uppdaterar tomrutans kolumn
            this.redoStack.push(move);       // Sparar draget i redoStack (så vi kan göra om)
            this.moves = Math.max(0, this.moves - 1); // Minskar antalet drag (men inte under 0)
            return true;                     // Ångringen lyckades
        }
    }

    // redo – gör om ett ångrat drag om det går
    public boolean redo() {
        if (this.redoStack.isEmpty()) {       // Om det inte finns något att göra om
            return false;
        } else {
            Move move = this.redoStack.pop(); // Tar bort senaste ångrade draget
            this.swap(move.fromRow, move.fromCol, move.toRow, move.toCol); // Gör draget igen
            this.emptyRow = move.fromRow;     // Uppdaterar tomrutans rad
            this.emptyCol = move.fromCol;     // Uppdaterar tomrutans kolumn
            this.undoStack.push(move);        // Lägger tillbaka draget i undoStack
            ++this.moves;                     // Ökar antal drag
            return true;                      // Redo lyckades
        }
    }

    // canUndo – kollar om det finns något att ångra
    public boolean canUndo() {
        return !this.undoStack.isEmpty(); // True om stacken inte är tom
    }

    // canRedo – kollar om det finns något att göra om
    public boolean canRedo() {
        return !this.redoStack.isEmpty(); // True om redoStack inte är tom
    }

    // shuffle – blandar brädet genom att göra slumpmässiga drag
    public void shuffle(int steps, Random rand) {
        for (int i = 0; i < steps; ++i) {   // Gör ett antal steg (t.ex. 1000 blandningar)
            int[][] neigh = new int[][]{    // Möjliga rutor runt den tomma
                    {this.emptyRow - 1, this.emptyCol}, // Upp
                    {this.emptyRow + 1, this.emptyCol}, // Ner
                    {this.emptyRow, this.emptyCol - 1}, // Vänster
                    {this.emptyRow, this.emptyCol + 1}  // Höger
            };
            int[] valid = new int[4];       // Sparar giltiga positioner
            int count = 0;                  // Räknar antal giltiga grannar

            // Tar bort de rutor som ligger utanför brädet
            for (int j = 0; j < neigh.length; ++j) {
                int rr = neigh[j][0];
                int cc = neigh[j][1];
                if (rr >= 0 && rr < this.SIZE && cc >= 0 && cc < this.SIZE) {
                    valid[count++] = j;
                }
            }

            // Väljer en slumpmässig giltig granne
            int pick = valid[rand.nextInt(count)];
            int nr = neigh[pick][0];
            int nc = neigh[pick][1];

            // Byter plats med den valda grannen
            this.swap(this.emptyRow, this.emptyCol, nr, nc);
            this.emptyRow = nr;
            this.emptyCol = nc;
        }

        this.moves = 0;           // Nollställer drag-räknaren
        this.undoStack.clear();   // Rensar undo
        this.redoStack.clear();   // Rensar redo
    }

    // isSolved – kollar om alla brickor står i rätt ordning (vinstläge)
    public boolean isSolved() {
        int expected = 1; // Nästa nummer som ska finnas

        for (int row = 0; row < this.SIZE; ++row) {
            for (int col = 0; col < this.SIZE; ++col) {

                // Sista rutan ska vara tom
                if (row == this.SIZE - 1 && col == this.SIZE - 1) {
                    if (!this.grid[row][col].isEmpty()) {
                        return false; // Om sista inte är tom, inte löst
                    }
                }
                // Kollar om siffran inte är den man förväntar sig
                else if (this.grid[row][col].value() != expected) {
                    return false; // Fel siffra → inte löst
                }

                ++expected; // Gå vidare till nästa siffra
            }
        }

        return true; // Allt stämmer → brädet är löst
    }

    // getMoves – hämtar hur många drag som gjorts
    public int getMoves() {
        return this.moves;
    }

    // get – hämtar en Tile (ruta) från en viss plats på brädet
    public Tile get(int row, int col) {
        return this.grid[row][col];
    }

    // size – hämtar storleken på brädet
    public int size() {
        return this.SIZE;
    }
}
