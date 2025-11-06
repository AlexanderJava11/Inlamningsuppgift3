import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;
import javax.swing.*;

// Klassen GameFrame är själva spel-fönstret för 15-spelet.
// Den visar först startskärmen och sen själva spelet.
public final class GameFrame extends JFrame {  // final betyder att den inte kan ärvas

    // Används för att växla mellan olika sidor (t.ex. start och spel)
    private final CardLayout cards = new CardLayout();

    // Panelen som håller alla sidor
    private final JPanel cardPanel;

    // Panel som visar själva spelet
    private JPanel gameWrapper;

    // Själva spelbrädet (logiken)
    private Board board;

    // Panelen som ritar ut spelbrädet
    private BoardPanel boardPanel;

    // Kontrollen som hanterar logiken (drag, ångra, tid osv.)
    private GameController controller;

    // Textfält som visar drag och tid
    private final JLabel statusLabel;

    // Konstruktor – skapar fönstret och startmenyn
    public GameFrame() {
        super("15-spelet"); // Sätter fönstrets titel

        // Skapar panelen som kan växla mellan olika sidor
        this.cardPanel = new JPanel(this.cards);

        // Skapar textfältet som visar status
        this.statusLabel = new JLabel("Drag: 0    Tid: 0s");

        // Stänger programmet när man klickar på stäng-knappen
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Skapar startskärmen
        StartPanel start = new StartPanel((e) -> this.showSizeDialogAndStart());
        start.setLayout(new BorderLayout());

        // Lägger till startpanelen i kortsystemet under namnet "Start"
        this.cardPanel.add(start, "Start");

        // Skapar en label som ska visa startbilden
        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Försöker hämta bilden "Start.png"
        URL u = this.getClass().getResource("Start.png");

        // Om bilden hittas – visa den
        if (u != null) {
            ImageIcon icon = new ImageIcon(u);  // Skapar en ikon av bilden
            Image scaled = icon.getImage().getScaledInstance(360, 240, Image.SCALE_SMOOTH); // Skalar ner bilden
            imgLabel.setIcon(new ImageIcon(scaled)); // Visar bilden
        } else {
            // Om bilden inte finns, visa text istället
            imgLabel.setText("Startbild saknas");
        }

        // Lägger till bilden i mitten av startskärmen
        start.add(imgLabel, BorderLayout.CENTER);

        // Skapar en text högst upp med instruktion
        JLabel info = new JLabel("Tryck på 'Starta spelet' för att börja och välj sedan storlek (2-8).", SwingConstants.CENTER);
        info.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Gör texten snyggare
        start.add(info, BorderLayout.NORTH);

        // Skapar knappen "Starta spelet!"
        JButton startButton = new JButton("Starta spelet!");

        // Skapar en panel som ska hålla knappen
        JPanel p = new JPanel();
        p.add(startButton);
        start.add(p, BorderLayout.SOUTH);

        // Skapar tom panel för själva spelet (visas senare)
        this.gameWrapper = new JPanel(new BorderLayout());
        this.cardPanel.add(this.gameWrapper, "Game");

        // Lägger till cardPanel i fönstret
        this.add(this.cardPanel);

        // När man klickar på startknappen startas spelet
        startButton.addActionListener((e) -> this.startGame());

        // Anpassar fönstrets storlek efter innehållet
        this.pack();

        // Centrerar fönstret på skärmen
        this.setLocationRelativeTo((Component) null);

        // Visar startskärmen först
        this.cards.show(this.cardPanel, "Start");

        // Gör fönstret synligt
        this.setVisible(true);
    }

    // Huvudmetoden – programmet startar här
    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame()); // Skapar fönstret
    }

    // Visar en dialog för att välja storlek och startar spelet
    private void startGame() {
        // Frågar användaren vilken storlek spelet ska ha
        String input = JOptionPane.showInputDialog(
                this,
                "Välj storlek (2-8)\nSkriv t.ex. 3 för 3x3:",
                "Välj storlek för spelet",
                JOptionPane.QUESTION_MESSAGE
        );

        // Om användaren inte tryckte "Avbryt"
        if (input != null) {
            int size;
            try {
                // Försök läsa in talet som användaren skrev
                size = Integer.parseInt(input.trim());
            } catch (NumberFormatException var15) {
                // Om användaren skrev något som inte är ett tal
                JOptionPane.showMessageDialog(this, "Skriv ett giltigt heltal.", "Fel", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Om talet är mellan 2 och 8 – godkänt
            if (size >= 2 && size <= 8) {

                // Skapar ett nytt bräde
                this.board = new Board(size);

                // Skapar panelen som ritar brädet
                this.boardPanel = new BoardPanel(this.board);

                // Skapar spelkontrollen (logik)
                this.controller = new GameController(this.board, this.boardPanel, this.statusLabel, this);

                // Kopplar kontrollern till panelen
                this.boardPanel.setController(this.controller);

                // Skapar menyraden
                JMenuBar menyBar = new JMenuBar();
                JMenu gameMeny = new JMenu("Spelet");

                // Skapar menyval
                JMenuItem newItem = new JMenuItem("Nytt spel");
                JMenuItem undoItem = new JMenuItem("Ångra");
                JMenuItem redoItem = new JMenuItem("Gör om");
                JMenuItem hsItem = new JMenuItem("Highscore");
                JMenuItem exitItem = new JMenuItem("Avsluta");

                // Kopplar menyvalen till funktioner
                newItem.addActionListener((a) -> this.controller.startNewGame());
                undoItem.addActionListener((a) -> this.controller.undo());
                redoItem.addActionListener((a) -> this.controller.redo());
                hsItem.addActionListener((a) -> this.controller.showHighscores());
                exitItem.addActionListener((a) -> System.exit(0));

                // Lägger till valen i menyn
                gameMeny.add(newItem);
                gameMeny.addSeparator();
                gameMeny.add(undoItem);
                gameMeny.add(redoItem);
                gameMeny.addSeparator();
                gameMeny.add(hsItem);
                gameMeny.addSeparator();
                gameMeny.add(exitItem);
                menyBar.add(gameMeny);

                // Lägger menyn i fönstret
                this.setJMenuBar(menyBar);

                // Skapar knappar under spelbrädet
                JButton newGameButton = new JButton("Nytt spel");
                newGameButton.addActionListener((a) -> this.controller.startNewGame());

                JButton undoButton = new JButton("Ångra");
                undoButton.addActionListener((a) -> this.controller.undo());

                JButton redoButton = new JButton("Gör om");
                redoButton.addActionListener((a) -> this.controller.redo());

                // Skapar panel för knappar och status
                JPanel south = new JPanel(new BorderLayout());
                JPanel buttonPanel = new JPanel();

                buttonPanel.add(newGameButton);
                buttonPanel.add(undoButton);
                buttonPanel.add(redoButton);

                // Status till vänster, knappar till höger
                south.add(buttonPanel, BorderLayout.EAST);
                south.add(this.statusLabel, BorderLayout.WEST);

                // Rensar tidigare innehåll och lägger till nya paneler
                this.gameWrapper.removeAll();
                this.gameWrapper.add(this.boardPanel, BorderLayout.CENTER);
                this.gameWrapper.add(south, BorderLayout.SOUTH);

                // Uppdaterar layouten
                this.cardPanel.revalidate();
                this.cardPanel.repaint();

                // Visar själva spelvyn
                this.cards.show(this.cardPanel, "Game");

                // Startar spelet
                this.controller.startNewGame();
                this.boardPanel.refresh();
                this.controller.updateStatus();

                // Timer som uppdaterar tiden var 0,5 sekund
                new Timer(500, (evt) -> this.controller.updateStatus()).start();

            } else {
                // Om talet inte är mellan 2 och 8 – felmeddelande
                JOptionPane.showMessageDialog(this, "Storleken måste vara mellan 2 och 8.", "Fel", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Går tillbaka till startskärmen
    public void backToStart() {
        this.cards.show(this.cardPanel, "Start");
    }

    // Visar dialogen för storlek och startar spelet
    public void showSizeDialogAndStart() {
        this.startGame();
    }
}
