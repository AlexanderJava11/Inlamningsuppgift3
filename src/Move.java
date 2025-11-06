// Klassen Move representerar ett drag i spelet (när en bricka flyttas)
public final class Move {   // final betyder att ingen annan klass kan ärva den

    // Sparar var brickan flyttas ifrån och vart den flyttas till
    public final int fromRow;  // Start-rad
    public final int fromCol;  // Start-kolumn
    public final int toRow;    // Mål-rad
    public final int toCol;    // Mål-kolumn

    // Skapar ett Move-objekt och sparar alla värden
    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;  // Sätt start-rad
        this.fromCol = fromCol;  // Sätt start-kolumn
        this.toRow = toRow;      // Sätt mål-rad
        this.toCol = toCol;      // Sätt mål-kolumn
    }
}
