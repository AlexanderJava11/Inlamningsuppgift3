// Klassen Move representerar ett drag (en förflyttning) i ett spel.
// Den är final, vilket betyder att ingen annan klass kan ärva från den.
// Detta är bra när man vill att klassen ska vara oföränderlig (immutable).
public final class Move {

    // Dessa fält beskriver varifrån och vart draget sker.
    // 'fromRow' och 'fromCol' är startpositionens rad och kolumn.
    // 'toRow' och 'toCol' är målpositionens rad och kolumn.
    public final int fromRow;
    public final int fromCol;
    public final int toRow;
    public final int toCol;
    
    // Konstruktor som skapar ett Move-objekt med givna koordinater.
    // Alla värden lagras direkt eftersom fälten är 'final' (de kan inte ändras senare)
    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }
}
