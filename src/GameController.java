import java.awt.Component;
import java.awt.Toolkit;
import java.util.List;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

// Klassen GameController hanterar spelets logik från UI-sidan.
// Den kommunicerar mellan Board (modell) och BoardPanel (vy),
// uppdaterar statusetiketten och hanterar tid/moves/highscore.
public final class GameController {
    // Referens till spelbrädet (modell).
    private final Board board;

    // Referens till panelen som ritar upp brädet.
    private final BoardPanel panel;

    // Etikett som visar status (antal drag och tid).
    private final JLabel status;

    // Förälder-komponent (används för dialoger, t.ex. GameFrame).
    private final Component parent;

    // Tidsstämpel när timern startades (millisekunder).
    private long startTime = 0L;

    // Ackumulerad tid i ms som gått innan en paus (om någon).
    private long elapsedBeforePause = 0L;

    // Timer som uppdaterar statusetiketten regelbundet.
    private Timer statusTimer;

    // Konstruktor som sätter upp controllern med nödvändiga referenser.
    public GameController(Board board, BoardPanel panel, JLabel status, Component parent) {
        this.board = board;
        this.panel = panel;
        this.status = status;
        this.parent = parent;
    }

    // startNewGame() initierar ett nytt spel:
    // löser brädet, shuffle:ar det, startar timer och uppdaterar vy/status.
    public void startNewGame() {
        // Ställ brädet i löst (målläge).
        this.board.initSolved();

        // Blanda brädet (5000 steg, med slump).
        this.board.shuffle(5000, new Random());

        // Starta timern för att mäta speltid.
        this.startTimer();

        // Be panelen rita om sig.
        this.panel.refresh();

        // Uppdatera statusetiketten direkt.
        this.updateStatus();

        // Stoppa eventuell gammal status-timer innan vi skapar en ny.
        if (this.statusTimer != null) {
            this.statusTimer.stop();
        }

        // Skapa en timer som uppdaterar status var 500 ms.
        this.statusTimer = new Timer(500, (e) -> this.updateStatus());
        this.statusTimer.start();
    }

    // startTimer() startar tidmätningen från noll.
    public void startTimer() {
        this.startTime = System.currentTimeMillis();
        this.elapsedBeforePause = 0L;
    }

    // getElapsedMs() returnerar förfluten tid i millisekunder.
    // Om timern inte körs returneras tiden innan paus.
    public long getElapsedMs() {
        return this.startTime == 0L
                ? this.elapsedBeforePause
                : this.elapsedBeforePause + (System.currentTimeMillis() - this.startTime);
    }

    // onTileClicked() anropas när en ruta klickas i BoardPanel.
    // Försöker göra ett drag, uppdaterar vy/status och kollar vinst.
    public void onTileClicked(int row, int col) {
        // Försök göra flytt på brädet.
        if (this.board.move(row, col)) {
            // Om flytt lyckades, rita om panelen och uppdatera status.
            this.panel.refresh();
            this.updateStatus();

            // Om spelet nu är löst, stoppa timern och hantera vinst.
            if (this.board.isSolved()) {
                if (this.statusTimer != null) {
                    this.statusTimer.stop();
                }

                long timeMS = this.getElapsedMs();

                // Kör onWin på Event Dispatch Thread för att visa dialoger säkert.
                SwingUtilities.invokeLater(() -> this.onWin(timeMS));
            }
        } else {
            // Om flytt inte är giltig, spela ett pip-ljud.
            Toolkit.getDefaultToolkit().beep();
        }
    }

    // onWin() hanterar vad som händer när spelaren vinner.
    // Visar vinstmeddelande, frågar efter namn för highscore och visar highscore.
    private void onWin(long timeMs) {
        // Skapa vinstmeddelande med drag och tid.
        String winMessage = String.format(
                "Grattis! Du löste spelet på %d drag och %d ms (≈ %.1f s).",
                this.board.getMoves(),
                timeMs,
                (double) timeMs / 1000.0F
        );

        // Visa ett informationsdialogfönster.
        JOptionPane.showMessageDialog(this.parent, winMessage, "Vinst!", JOptionPane.INFORMATION_MESSAGE);

        // Fråga efter spelarens namn för highscore.
        String namn = JOptionPane.showInputDialog(this.parent, "Skriv ditt namn för highscore:", "Highscore", JOptionPane.PLAIN_MESSAGE);
        if (namn != null && !namn.trim().isEmpty()) {
            // Om namn angavs, lägg till i highscores.
            Highscore.addEntry(new Highscore.Entry(namn.trim(), this.board.getMoves(), timeMs));
        }

        // Visa highscore-listan efter vinst.
        this.showHighscores();

        // Fråga om spelaren vill spela igen.
        int again = JOptionPane.showConfirmDialog(this.parent, "Vill du spela igen?", "Spela igen", JOptionPane.YES_NO_OPTION);
        if (again == JOptionPane.YES_OPTION) {
            // Om föräldern är GameFrame, gå tillbaka till startskärmen.
            if (this.parent instanceof GameFrame) {
                ((GameFrame) this.parent).backToStart();
            }
        } else {
            // Annars avsluta programmet.
            System.exit(0);
        }
    }

    // showHighscores() läser in och visar highscore-listan i en dialog.
    public void showHighscores() {
        List<Highscore.Entry> list = Highscore.load();

        // Om ingen highscore finns, visa meddelande.
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this.parent, "Inga highscore ännu.", "Highscore", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Bygg en textsträng med topplistan.
            StringBuilder sb = new StringBuilder();
            int i = 1;

            for (Highscore.Entry entry : list) {
                sb.append(i++).append(". ").append(entry.toString()).append("\n");
                // Bryt efter 10 poster om det finns fler.
                if (i > 10) {
                    break;
                }
            }

            // Visa highscore-listan i en dialog.
            JOptionPane.showMessageDialog(this.parent, sb.toString(), "Highscore", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // undo() försöker ångra senaste draget via Board.
    // Om det lyckas uppdateras vy och status, annars spela pip.
    public void undo() {
        if (this.board.undo()) {
            this.panel.refresh();
            this.updateStatus();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    // redo() försöker göra om ett tidigare ångrat drag.
    // Om det lyckas uppdateras vy och status, annars spela pip.
    public void redo() {
        if (this.board.redo()) {
            this.panel.refresh();
            this.updateStatus();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    // updateStatus() uppdaterar statusetiketten med aktuella drag och tid i sekunder.
    public void updateStatus() {
        String t = String.format("Drag: %d      Tid:%ds", this.board.getMoves(), this.getElapsedMs() / 1000L);
        this.status.setText(t);
    }
}
