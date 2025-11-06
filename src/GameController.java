import java.awt.Component;
import java.awt.Toolkit;
import java.util.List;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

// Klassen GameController styr själva spelet.
// Den sköter logiken mellan spelbrädet (Board) och grafiken (BoardPanel).
// Den håller också koll på tid, drag och highscore.
public final class GameController {

    private final Board board;       // Själva spelbrädet (logiken)
    private final BoardPanel panel;  // Panelen som visar spelet på skärmen
    private final JLabel status;     // Textfält som visar drag och tid
    private final Component parent;  // Fönstret (GameFrame) som äger spelet

    private long startTime = 0L;           // När spelet startade (tid i millisekunder)
    private long elapsedBeforePause = 0L;  // Tid som gått innan paus (om det finns)
    private Timer statusTimer;             // Timer som uppdaterar status-texten

    // Konstruktorn kopplar ihop spelet med panelen och status-texten
    public GameController(Board board, BoardPanel panel, JLabel status, Component parent) {
        this.board = board;
        this.panel = panel;
        this.status = status;
        this.parent = parent;
    }

    // Startar ett nytt spel: blandar brädet, nollställer tid och uppdaterar allt
    public void startNewGame() {
        this.board.initSolved(); // Starta från ett löst bräde
        this.board.shuffle(5000, new Random()); // Blanda slumpmässigt

        this.startTimer(); // Starta tidtagningen
        this.panel.refresh(); // Rita om spelbrädet
        this.updateStatus();  // Uppdatera status-texten

        // Stoppa eventuell gammal timer innan vi skapar en ny
        if (this.statusTimer != null) {
            this.statusTimer.stop();
        }

        // Skapa en ny timer som uppdaterar status var 0,5 sekund
        this.statusTimer = new Timer(500, (e) -> this.updateStatus());
        this.statusTimer.start();
    }

    // Startar tidtagningen från noll
    public void startTimer() {
        this.startTime = System.currentTimeMillis();
        this.elapsedBeforePause = 0L;
    }

    // Räknar ut hur lång tid som gått i millisekunder
    public long getElapsedMs() {
        return this.startTime == 0L
                ? this.elapsedBeforePause
                : this.elapsedBeforePause + (System.currentTimeMillis() - this.startTime);
    }

    // Körs när man klickar på en bricka i spelet
    public void onTileClicked(int row, int col) {
        // Försök flytta brickan
        if (this.board.move(row, col)) {
            this.panel.refresh(); // Rita om efter drag
            this.updateStatus();  // Uppdatera status

            // Kolla om spelet är löst
            if (this.board.isSolved()) {
                if (this.statusTimer != null) {
                    this.statusTimer.stop(); // Stoppa tiden
                }

                long timeMS = this.getElapsedMs(); // Hämta tiden som gått

                // Visa vinstmeddelande (körs säkert i Swing-tråd)
                SwingUtilities.invokeLater(() -> this.onWin(timeMS));
            }
        } else {
            // Om draget inte är tillåtet – spela ett pip
            Toolkit.getDefaultToolkit().beep();
        }
    }

    // Körs när man vinner spelet
    private void onWin(long timeMs) {
        // Skapa meddelande som visar resultatet
        String winMessage = String.format(
                "Grattis! Du löste spelet på %d drag och %d ms (≈ %.1f s).",
                this.board.getMoves(),
                timeMs,
                (double) timeMs / 1000.0F
        );

        // Visa dialogruta med resultat
        JOptionPane.showMessageDialog(this.parent, winMessage, "Vinst!", JOptionPane.INFORMATION_MESSAGE);

        // Fråga efter namn för highscore-listan
        String namn = JOptionPane.showInputDialog(this.parent, "Skriv ditt namn för highscore:", "Highscore", JOptionPane.PLAIN_MESSAGE);

        // Om spelaren skrev in ett namn, spara resultatet
        if (namn != null && !namn.trim().isEmpty()) {
            Highscore.addEntry(new Highscore.Entry(namn.trim(), this.board.getMoves(), timeMs));
        }

        // Visa highscore-listan
        this.showHighscores();

        // Fråga om spelaren vill spela igen
        int again = JOptionPane.showConfirmDialog(this.parent, "Vill du spela igen?", "Spela igen", JOptionPane.YES_NO_OPTION);

        if (again == JOptionPane.YES_OPTION) {
            // Om användaren vill spela igen → gå till startskärmen
            if (this.parent instanceof GameFrame) {
                ((GameFrame) this.parent).backToStart();
            }
        } else {
            // Om inte → avsluta programmet
            System.exit(0);
        }
    }

    // Visar highscore-listan i en dialogruta
    public void showHighscores() {
        List<Highscore.Entry> list = Highscore.load(); // Läs in från fil

        if (list.isEmpty()) {
            // Om det inte finns några resultat än
            JOptionPane.showMessageDialog(this.parent, "Inga highscore ännu.", "Highscore", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Bygg upp en text med topplistan
            StringBuilder sb = new StringBuilder();
            int i = 1;

            for (Highscore.Entry entry : list) {
                sb.append(i++).append(". ").append(entry.toString()).append("\n");
                if (i > 10) break; // Visa max 10
            }

            // Visa i en meddelanderuta
            JOptionPane.showMessageDialog(this.parent, sb.toString(), "Highscore", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Ångrar senaste draget (om möjligt)
    public void undo() {
        if (this.board.undo()) {
            this.panel.refresh();
            this.updateStatus();
        } else {
            Toolkit.getDefaultToolkit().beep(); // Om inget att ångra
        }
    }

    // Gör om ett ångrat drag (om möjligt)
    public void redo() {
        if (this.board.redo()) {
            this.panel.refresh();
            this.updateStatus();
        } else {
            Toolkit.getDefaultToolkit().beep(); // Om inget att göra om
        }
    }

    // Uppdaterar texten som visar drag och tid
    public void updateStatus() {
        String t = String.format("Drag: %d      Tid:%ds", this.board.getMoves(), this.getElapsedMs() / 1000L);
        this.status.setText(t);
    }
}
