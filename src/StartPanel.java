import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Klassen StartPanel är startsidan i spelet (en del av GUI:t)
public class StartPanel extends JPanel {   // Den ärver från JPanel så den kan visas i ett fönster

    // Konstruktorn tar emot en ActionListener som ska köras när man trycker på "Börja spela"
    public StartPanel(ActionListener onStart) {

        // Sätter layouten till BorderLayout – det betyder att vi kan placera saker i mitten, botten osv.
        this.setLayout(new BorderLayout());

        // Förklarar en JLabel som ska visa bilden på startsidan
        JLabel image;
        try {
            // Försöker ladda en bild som heter "Start.png" från samma mapp som klassen ligger i
            image = new JLabel(new ImageIcon(this.getClass().getResource("Start.png")));
        } catch (Exception e) {
            // Om bilden inte finns eller inte kan laddas
            image = new JLabel("Startbild inte hittad"); // Visa en text istället
            image.setHorizontalAlignment(0); // Centrerar texten i JLabel
        }

        // Lägger till bilden (eller texten) i mitten av panelen
        this.add(image, "Center");

        // Skapar en knapp med texten "Börja spela"
        JButton startButton = new JButton("Börja spela");

        // Lägger till lyssnaren som körs när knappen trycks på
        startButton.addActionListener(onStart);

        // Skapar en ny panel som ska ligga längst ner (söder)
        JPanel south = new JPanel();

        // Lägger till knappen i den panelen
        south.add(startButton);

        // Lägger till panelen längst ner i StartPanel
        this.add(south, "South");
    }
}
