import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public final class BoardPanel extends JPanel {
    private final JButton[][] buttons;
    private final Board board;
    private GameController controller;

    public BoardPanel(Board board) {
        this.board = board;
        int n = board.size();
        this.buttons = new JButton[n][n];
        this.setLayout(new GridLayout(n, n, 6, 6));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.createButtons();
        this.refresh();
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    private void createButtons() {
        int n = this.board.size();

        for(int row = 0; row < n; ++row) {
            for(int col = 0; col < n; ++col) {
                final int rr = row, cc = col;
                JButton button = new JButton();
                button.setFont(button.getFont().deriveFont(24.0F));
                button.setFocusable(false);
                button.addActionListener((e) -> {
                    if (this.controller != null) controller.onTileClicked(rr, cc);


                });
                this.buttons[row][col] = button;
                this.add(button);
            }
        }

    }

    public void refresh() {
        int n = this.board.size();

        for(int row = 0; row < n; ++row) {
            for(int col = 0; col < n; ++col) {
                Tile tile = this.board.get(row, col);
                JButton button = this.buttons[row][col];
                button.setText(tile.toString());
                if (tile.isEmpty()) {
                    button.setEnabled(false);
                    button.setBackground(Color.LIGHT_GRAY);
                    button.setForeground(Color.DARK_GRAY);
                    button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                } else {
                    button.setEnabled(true);
                    button.setBackground((Color)null);
                    button.setForeground((Color)null);
                }
            }
        }

        this.repaint();
    }
}
