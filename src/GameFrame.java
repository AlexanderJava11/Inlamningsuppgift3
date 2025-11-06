import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;
import javax.swing.*;

/**
 * Klassen GameFrame representerar själva spel-fönstret för "15-spelet".
 * Den hanterar både startmenyn och spelvyn med hjälp av ett CardLayout.
 * Klassen är final, vilket betyder att den inte kan ärvas.
 */
public final class GameFrame extends JFrame {

    // CardLayout används för att växla mellan olika "sidor" i fönstret (t.ex. startskärm och spel).
    private final CardLayout cards = new CardLayout();

    // Panelen som innehåller alla sidor (startmeny och spel).
    private final JPanel cardPanel;

    // Panel som håller ihop spelets olika delar (bräde, knappar, statusfält).
    private JPanel gameWrapper;

    // Spelbrädet där själva spelet sker.
    private Board board;

    // Panel som ritar upp spelbrädet grafiskt.
    private BoardPanel boardPanel;

    // Kontroller som hanterar spelets logik (t.ex. drag, ångra, tid).
    private GameController controller;

    // Etikett som visar status (antal drag och tid).
    private final JLabel statusLabel;

    // Konstruktor som skapar spelets huvudfönster och startskärmen.
    public GameFrame() {
        super("15-spelet"); // Sätter titeln på fönstret.

        // Skapar huvudpanelen med kortsystemet (CardLayout).
        this.cardPanel = new JPanel(this.cards);

        // Skapar statusetiketten som visar drag och tid.
        this.statusLabel = new JLabel("Drag: 0    Tid: 0s");

        // Stänger programmet när man trycker på stäng-knappen.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Skapar startskärmen.
        StartPanel start = new StartPanel((e) -> this.showSizeDialogAndStart());
        start.setLayout(new BorderLayout());

        // Lägger till startpanelen i kortsystemet under namnet "Start".
        this.cardPanel.add(start, "Start");

        // Skapar en etikett för startbilden.
        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Försöker hämta bilden "Start.png" från projektresurserna.
        URL u = this.getClass().getResource("Start.png");

        // Om bilden finns, skala den och visa den.
        if (u != null) {
            ImageIcon icon = new ImageIcon(u);
            Image scaled = icon.getImage().getScaledInstance(360, 240, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } else {
            // Om bilden inte hittas, visa text istället.
            imgLabel.setText("Startbild saknas");
        }

        // Lägger till bilden i mitten av startskärmen.
        start.add(imgLabel, BorderLayout.CENTER);

        // Skapar en informationsetikett högst upp på startskärmen.
        JLabel info = new JLabel("Tryck på 'Starta spelet' för att börja och välj sedan storlek (2-8).", SwingConstants.CENTER);
        info.setFont(new Font("SansSerif", Font.PLAIN, 14));
        start.add(info, BorderLayout.NORTH);

        // Skapar en knapp för att starta spelet.
        JButton startButton = new JButton("Starta spelet!");

        // Panel som håller startknappen.
        JPanel p = new JPanel();
        p.add(startButton);
        start.add(p, BorderLayout.SOUTH);

        // Skapar panelen där spelet ska visas senare.
        this.gameWrapper = new JPanel(new BorderLayout());
        this.cardPanel.add(this.gameWrapper, "Game");

        // Lägger till huvudpanelen (cardPanel) i fönstret.
        this.add(this.cardPanel);

        // När man klickar på startknappen anropas startGame().
        startButton.addActionListener((e) -> this.startGame());

        // Anpassar fönstret till innehållet.
        this.pack();

        // Centrerar fönstret på skärmen.
        this.setLocationRelativeTo((Component) null);

        // Visar startskärmen först.
        this.cards.show(this.cardPanel, "Start");

        // Gör fönstret synligt.
        this.setVisible(true);
    }

    // Huvudmetoden som startar programmet.
    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame());
    }

    // startGame() visar en dialogruta för att välja storlek och startar sedan spelet.
    private void startGame() {
        // Ber användaren skriva in storleken på spelet (t.ex. 3 för 3x3).
        String input = JOptionPane.showInputDialog(
                this,
                "Välj storlek (2-8)\nSkriv t.ex. 3 för 3x3:",
                "Välj storlek för spelet",
                JOptionPane.QUESTION_MESSAGE
        );

        // Om användaren inte avbröt.
        if (input != null) {
            int size;
            try {
                // Försök tolka inmatningen som ett heltal.
                size = Integer.parseInt(input.trim());
            } catch (NumberFormatException var15) {
                // Om det inte är ett giltigt heltal, visa felmeddelande.
                JOptionPane.showMessageDialog(this, "Skriv ett giltigt heltal.", "Fel", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kontrollera att storleken är mellan 2 och 8.
            if (size >= 2 && size <= 8) {

                // Skapa ett nytt spelbräde.
                this.board = new Board(size);

                // Skapa panelen som ritar upp brädet.
                this.boardPanel = new BoardPanel(this.board);

                // Skapa spelets kontroller.
                this.controller = new GameController(this.board, this.boardPanel, this.statusLabel, this);

                // Koppla kontrollern till panelen.
                this.boardPanel.setController(this.controller);

                // --- Skapar menyraden ---
                JMenuBar menyBar = new JMenuBar();
                JMenu gameMeny = new JMenu("Spelet");

                // Skapar menyval.
                JMenuItem newItem = new JMenuItem("Nytt spel");
                JMenuItem undoItem = new JMenuItem("Ångra");
                JMenuItem redoItem = new JMenuItem("Gör om");
                JMenuItem hsItem = new JMenuItem("Highscore");
                JMenuItem exitItem = new JMenuItem("Avsluta");

                // Kopplar menyvalen till funktioner.
                newItem.addActionListener((a) -> this.controller.startNewGame());
                undoItem.addActionListener((a) -> this.controller.undo());
                redoItem.addActionListener((a) -> this.controller.redo());
                hsItem.addActionListener((a) -> this.controller.showHighscores());
                exitItem.addActionListener((a) -> System.exit(0));

                // Lägger till menyobjekten i menyn.
                gameMeny.add(newItem);
                gameMeny.addSeparator();
                gameMeny.add(undoItem);
                gameMeny.add(redoItem);
                gameMeny.addSeparator();
                gameMeny.add(hsItem);
                gameMeny.addSeparator();
                gameMeny.add(exitItem);
                menyBar.add(gameMeny);

                // Lägger till menyn i fönstret.
                this.setJMenuBar(menyBar);

                // --- Skapar knappar under spelbrädet ---
                JButton newGameButton = new JButton("Nytt spel");
                newGameButton.addActionListener((a) -> this.controller.startNewGame());

                JButton undoButton = new JButton("Ångra");
                undoButton.addActionListener((a) -> this.controller.undo());

                JButton redoButton = new JButton("Gör om");
                redoButton.addActionListener((a) -> this.controller.redo());

                // Panel för knapparna.
                JPanel south = new JPanel(new BorderLayout());
                JPanel buttonPanel = new JPanel();

                buttonPanel.add(newGameButton);
                buttonPanel.add(undoButton);
                buttonPanel.add(redoButton);

                // Lägger knappar till höger, status till vänster.
                south.add(buttonPanel, BorderLayout.EAST);
                south.add(this.statusLabel, BorderLayout.WEST);

                // Tar bort tidigare innehåll och lägger till spelvyn.
                this.gameWrapper.removeAll();
                this.gameWrapper.add(this.boardPanel, BorderLayout.CENTER);
                this.gameWrapper.add(south, BorderLayout.SOUTH);

                // Uppdaterar layouten.
                this.cardPanel.revalidate();
                this.cardPanel.repaint();

                // Visar spelvyn.
                this.cards.show(this.cardPanel, "Game");

                // Startar ett nytt spel.
                this.controller.startNewGame();
                this.boardPanel.refresh();
                this.controller.updateStatus();

                // Startar en timer som uppdaterar status var 0,5 sekund.
                new Timer(500, (evt) -> this.controller.updateStatus()).start();

            } else {
                // Om storleken inte är mellan 2 och 8, visa fel.
                JOptionPane.showMessageDialog(this, "Storleken måste vara mellan 2 och 8.", "Fel", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Metoden backToStart() växlar tillbaka till startskärmen.
    public void backToStart() {
        this.cards.show(this.cardPanel, "Start");
    }

    // Metoden showSizeDialogAndStart() visar dialogrutan för storlek och startar spelet.
    public void showSizeDialogAndStart() {
        this.startGame();
    }
}
