import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public final class GameFrame extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel;
    private JPanel gameWrapper;
    private Board board;
    private BoardPanel boardPanel;
    private GameController controller;
    private final JLabel statusLabel;

    public GameFrame() {
        super("15-spelet");
        this.cardPanel = new JPanel(this.cards);
        this.statusLabel = new JLabel("Drag: 0    Tid: 0s");
        this.setDefaultCloseOperation(3);
        StartPanel start = new StartPanel((e) -> this.showSizeDialogAndStart());
        start.setLayout(new BorderLayout());
        this.cardPanel.add(start, "Start");
        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(0);
        URL u = this.getClass().getResource("Start.png");
        if (u != null) {
            ImageIcon icon = new ImageIcon(u);
            Image scaled = icon.getImage().getScaledInstance(360, 240, 4);
            imgLabel.setIcon(new ImageIcon(scaled));
        } else {
            imgLabel.setText("Startbild saknas");
        }

        start.add(imgLabel, "Center");
        JLabel info = new JLabel("Tryck på 'Starta spelet' för att börja och välj sedan storlek (2-8).", 0);
        info.setFont(new Font("SansSerif", 0, 14));
        start.add(info, "North");
        JButton startButton = new JButton("Starta spelet!");
        JPanel p = new JPanel();
        p.add(startButton);
        start.add(p, "South");
        this.gameWrapper = new JPanel(new BorderLayout());
        this.cardPanel.add(this.gameWrapper, "Game");
        this.add(this.cardPanel);
        startButton.addActionListener((e) -> this.startGame());
        this.pack();
        this.setLocationRelativeTo((Component)null);
        this.cards.show(this.cardPanel, "Start");
        this.setVisible(true);
    }

    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame());
    }

    private void startGame() {
        String input = JOptionPane.showInputDialog(this, "Välj storlek (2-8)\nSkriv t.ex. 3 för 3x3:", "Välj storlek för spelet", 3);
        if (input != null) {
            int size;
            try {
                size = Integer.parseInt(input.trim());
            } catch (NumberFormatException var15) {
                JOptionPane.showMessageDialog(this, "Skriv ett giltigt heltal.", "Fel", 0);
                return;
            }

            if (size >= 2 && size <= 8) {
                this.board = new Board(size);
                this.boardPanel = new BoardPanel(this.board);
                this.controller = new GameController(this.board, this.boardPanel, this.statusLabel, this);
                this.boardPanel.setController(this.controller);
                JMenuBar menyBar = new JMenuBar();
                JMenu gameMeny = new JMenu("Spelet");
                JMenuItem newItem = new JMenuItem("Nytt spel");
                JMenuItem undoItem = new JMenuItem("Ångra");
                JMenuItem redoItem = new JMenuItem("Gör om");
                JMenuItem hsItem = new JMenuItem("Highscore");
                JMenuItem exitItem = new JMenuItem("Avsluta");
                newItem.addActionListener((a) -> this.controller.startNewGame());
                undoItem.addActionListener((a) -> this.controller.undo());
                redoItem.addActionListener((a) -> this.controller.redo());
                hsItem.addActionListener((a) -> this.controller.showHighscores());
                exitItem.addActionListener((a) -> System.exit(0));
                gameMeny.add(newItem);
                gameMeny.addSeparator();
                gameMeny.add(undoItem);
                gameMeny.add(redoItem);
                gameMeny.addSeparator();
                gameMeny.add(hsItem);
                gameMeny.addSeparator();
                gameMeny.add(exitItem);
                menyBar.add(gameMeny);
                this.setJMenuBar(menyBar);
                JButton newGameButton = new JButton("Nytt spel");
                newGameButton.addActionListener((a) -> this.controller.startNewGame());
                JButton undoButton = new JButton("Ångra");
                undoButton.addActionListener((a) -> this.controller.undo());
                JButton redoButton = new JButton("Gör om");
                redoButton.addActionListener((a) -> this.controller.redo());
                JPanel south = new JPanel(new BorderLayout());
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(newGameButton);
                buttonPanel.add(undoButton);
                buttonPanel.add(redoButton);
                south.add(buttonPanel, "East");
                south.add(this.statusLabel, "West");
                this.gameWrapper.removeAll();
                this.gameWrapper.add(this.boardPanel, "Center");
                this.gameWrapper.add(south, "South");
                this.cardPanel.revalidate();
                this.cardPanel.repaint();
                this.cards.show(this.cardPanel, "Game");
                this.controller.startNewGame();
                this.boardPanel.refresh();
                this.controller.updateStatus();
                (new Timer(500, (evt) -> this.controller.updateStatus())).start();
            } else {
                JOptionPane.showMessageDialog(this, "Storleken måste vara mellan 2 och 8.", "Fel", 0);
            }
        }
    }

    public void backToStart() {
        this.cards.show(this.cardPanel, "Start");
    }

    public void showSizeDialogAndStart() {
        this.startGame();
    }
}
