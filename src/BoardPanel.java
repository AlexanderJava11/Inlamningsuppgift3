import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

// Klassen BoardPanel visar själva spelbrädet på skärmen.
// Den innehåller alla rutor (Tiles) som knappar och uppdateras när man flyttar brickor.
public final class BoardPanel extends JPanel {

    private final JButton[][] buttons; // Knapparna som representerar rutorna på brädet
    private final Board board;         // Själva spelbrädet (modell)
    private GameController controller; // Hanterar klick och logik

    // Skapar panelen och bygger upp rutnätet
    public BoardPanel(Board board) {
        this.board = board; // Spara referensen till brädet

        int n = board.size(); // Hämta storleken (t.ex. 4 för 4x4)

        // Skapa en matris med knappar i samma storlek som brädet
        this.buttons = new JButton[n][n];

        // Sätt layouten till rutnät (GridLayout) med lite mellanrum
        this.setLayout(new GridLayout(n, n, 6, 6));

        // Lägg lite tomrum runt hela brädet
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Skapa knapparna
        this.createButtons();

        // Uppdatera knapparnas text och färg
        this.refresh();
    }

    // Kopplar panelen till GameController så klick fungerar
    public void setController(GameController controller) {
        this.controller = controller;
    }

    // Skapar alla knappar i brädet
    private void createButtons() {
        int n = this.board.size();

        // Loopa genom varje rad och kolumn
        for (int row = 0; row < n; ++row) {
            for (int col = 0; col < n; ++col) {
                final int rr = row; // Spara rad
                final int cc = col; // Spara kolumn

                JButton button = new JButton(); // Skapa ny knapp

                // Gör texten på knappen större
                button.setFont(button.getFont().deriveFont(24.0F));

                // Ta bort fokusmarkering (ingen blå ram när man klickar)
                button.setFocusable(false);

                // När man klickar – meddela GameController
                button.addActionListener((e) -> {
                    if (this.controller != null) controller.onTileClicked(rr, cc);
                });

                // Spara knappen i listan
                this.buttons[row][col] = button;

                // Lägg till knappen på panelen så den syns
                this.add(button);
            }
        }
    }

    // Uppdaterar alla knappar beroende på hur brädet ser ut just nu
    public void refresh() {
        int n = this.board.size();

        // Gå igenom varje ruta
        for (int row = 0; row < n; ++row) {
            for (int col = 0; col < n; ++col) {
                Tile tile = this.board.get(row, col);   // Hämta brickan
                JButton button = this.buttons[row][col]; // Hämta motsvarande knapp

                // Sätt texten på knappen (eller tom om det är den tomma rutan)
                button.setText(tile.toString());

                if (tile.isEmpty()) {
                    // Om det är den tomma rutan (0) → grå och inaktiv
                    button.setEnabled(false);
                    button.setBackground(Color.LIGHT_GRAY);
                    button.setForeground(Color.DARK_GRAY);
                    button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                } else {
                    // Annars → normal färg och aktiv
                    button.setEnabled(true);
                    button.setBackground((Color) null);
                    button.setForeground((Color) null);
                }
            }
        }

        // Rita om panelen så förändringen syns
        this.repaint();
    }
}
