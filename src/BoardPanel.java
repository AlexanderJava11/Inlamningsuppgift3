import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

// Klassen BoardPanel representerar den grafiska delen av spelbrädet.
// Den visar alla rutor (Tiles) som knappar i ett rutnät och uppdateras när spelet ändras.
public final class BoardPanel extends JPanel {

    // En tvådimensionell array med knappar – en för varje ruta på brädet.
    private final JButton[][] buttons;

    // Referens till spelbrädet (modellen) som innehåller alla rutor.
    private final Board board;

    // Referens till GameController som hanterar logiken när en ruta klickas.
    private GameController controller;

    // Konstruktor som tar emot ett Board-objekt och bygger upp gränssnittet.
    public BoardPanel(Board board) {
        this.board = board; // Sparar referensen till brädet.

        int n = board.size(); // Hämtar storleken på brädet (t.ex. 4 för 4x4).

        // Skapar en 2D-array med knappar i samma storlek som brädet.
        this.buttons = new JButton[n][n];

        // Sätter layouten så att knapparna ordnas i ett rutnät (GridLayout).
        this.setLayout(new GridLayout(n, n, 6, 6)); // 6 px mellanrum mellan knappar.

        // Lägger lite extra tomrum runt brädet.
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Skapar knapparna och lägger till dem i panelen.
        this.createButtons();

        // Uppdaterar knapparnas text och utseende efter brädets aktuella tillstånd.
        this.refresh();
    }

    // setController() används för att koppla panelen till en GameController.
    public void setController(GameController controller) {
        this.controller = controller;
    }

    // createButtons() skapar alla knappar för brädet och lägger till klicklyssnare.
    private void createButtons() {
        int n = this.board.size();

        // Loopa igenom alla rader och kolumner i brädet.
        for (int row = 0; row < n; ++row) {
            for (int col = 0; col < n; ++col) {
                // Behåller lokala kopior av positionerna (för användning i lambda).
                final int rr = row, cc = col;

                // Skapar en ny knapp.
                JButton button = new JButton();

                // Gör texten på knappen större för bättre synlighet.
                button.setFont(button.getFont().deriveFont(24.0F));

                // Gör så att knappen inte får fokusmarkering (ingen "ram" vid klick).
                button.setFocusable(false);

                // När man klickar på en knapp, anropa controllerns onTileClicked().
                button.addActionListener((e) -> {
                    if (this.controller != null) controller.onTileClicked(rr, cc);
                });

                // Sparar knappen i arrayen.
                this.buttons[row][col] = button;

                // Lägger till knappen i panelen (så att den syns i GUI:t).
                this.add(button);
            }
        }
    }

    // refresh() uppdaterar alla knappars text, färg och tillstånd
    // baserat på brädets aktuella position (t.ex. om en ruta är tom).
    public void refresh() {
        int n = this.board.size();

        // Loopa igenom alla rutor.
        for (int row = 0; row < n; ++row) {
            for (int col = 0; col < n; ++col) {
                // Hämtar rutan (Tile) på denna position.
                Tile tile = this.board.get(row, col);

                // Hämtar motsvarande knapp.
                JButton button = this.buttons[row][col];

                // Sätter knappens text till rutan värde (eller tom sträng om tom ruta).
                button.setText(tile.toString());

                // Om rutan är tom (värde 0), gråa ut knappen.
                if (tile.isEmpty()) {
                    button.setEnabled(false); // Gör knappen icke-klickbar.
                    button.setBackground(Color.LIGHT_GRAY); // Grå bakgrund.
                    button.setForeground(Color.DARK_GRAY);   // Mörk text.
                    button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Tunn kant.
                } else {
                    // Om rutan har ett värde, gör den aktiv och standardfärgad.
                    button.setEnabled(true);
                    button.setBackground((Color) null);
                    button.setForeground((Color) null);
                }
            }
        }

        // Tvingar panelen att ritas om (uppdateras visuellt).
        this.repaint();
    }
}