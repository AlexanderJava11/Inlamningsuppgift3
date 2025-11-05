import java.awt.Component;
import java.awt.Toolkit;
import java.util.List;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public final class GameController {
    private final Board board;
    private final BoardPanel panel;
    private final JLabel status;
    private final Component parent;
    private long startTime = 0L;
    private long elapsedBeforePause = 0L;
    private Timer statusTimer;

    public GameController(Board board, BoardPanel panel, JLabel status, Component parent) {
        this.board = board;
        this.panel = panel;
        this.status = status;
        this.parent = parent;
    }

    public void startNewGame() {
        this.board.initSolved();
        this.board.shuffle(5000, new Random());
        if (this.panel != null) {
        }

        this.startTimer();
        this.panel.refresh();
        this.updateStatus();
        if (this.statusTimer != null) {
            this.statusTimer.stop();
        }

        this.statusTimer = new Timer(500, (e) -> this.updateStatus());
        this.statusTimer.start();
    }

    public void startTimer() {
        this.startTime = System.currentTimeMillis();
        this.elapsedBeforePause = 0L;
    }

    public long getElapsedMs() {
        return this.startTime == 0L ? this.elapsedBeforePause : this.elapsedBeforePause + (System.currentTimeMillis() - this.startTime);
    }

    public void onTileClicked(int row, int col) {
        if (this.board.move(row, col)) {
            this.panel.refresh();
            this.updateStatus();
            if (this.board.isSolved()) {
                if (this.statusTimer != null) {
                    this.statusTimer.stop();
                }

                long timeMS = this.getElapsedMs();
                SwingUtilities.invokeLater(() -> this.onWin(timeMS));
            }
        } else {
            Toolkit.getDefaultToolkit().beep();
        }

    }

    private void onWin(long timeMs) {
        String winMessage = String.format("Grattis! Du löste spelet på %d drag och %d ms (≈ %.1f s).", this.board.getMoves(), timeMs, (double)timeMs / (double)1000.0F);
        JOptionPane.showMessageDialog(this.parent, winMessage, "Vinst!", 1);
        String namn = JOptionPane.showInputDialog(this.parent, "Skriv ditt namn för highscore:", "Highscore", -1);
        if (namn != null && !namn.trim().isEmpty()) {
            Highscore.addEntry(new Highscore.Entry(namn.trim(), this.board.getMoves(), timeMs));
        }

        this.showHighscores();
        int again = JOptionPane.showConfirmDialog(this.parent, "Vill du spela igen?", "Spela igen", 0);
        if (again == 0) {
            if (this.parent instanceof GameFrame) {
                ((GameFrame)this.parent).backToStart();
            }
        } else {
            System.exit(0);
        }

    }

    public void showHighscores() {
        List<Highscore.Entry> list = Highscore.load();
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this.parent, "Inga highscore ännu.", "Highscore", 1);
        } else {
            StringBuilder sb = new StringBuilder();
            int i = 1;

            for(Highscore.Entry entry : list) {
                sb.append(i++).append(". ").append(entry.toString()).append("\n");
                if (i > 10) {
                    break;
                }
            }

            JOptionPane.showMessageDialog(this.parent, sb.toString(), "Highscore", 1);
        }
    }

    public void undo() {
        if (this.board.undo()) {
            this.panel.refresh();
            this.updateStatus();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }

    }

    public void redo() {
        if (this.board.redo()) {
            this.panel.refresh();
            this.updateStatus();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }

    }

    public void updateStatus() {
        String t = String.format("Drag: %d      Tid:%ds", this.board.getMoves(), this.getElapsedMs() / 1000L);
        this.status.setText(t);
    }
}
