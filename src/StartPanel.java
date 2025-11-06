import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Klassen StartPanel är en del av GUI:t (Graphical User Interface) och utgör startsidan i spelet.
// Den ärver från JPanel, vilket betyder att den är en panel som kan läggas till i ett JFrame
public class StartPanel extends JPanel {
    
    // Konstruktorn tar emot en ActionListener som parameter.
    // Detta gör att du kan skicka in vad som ska hända när användaren klickar på "Börja spela".
    public StartPanel(ActionListener onStart) {
       
        // Sätter layouten till BorderLayout så att vi enkelt kan placera komponenter i t.ex. mitten och botten.
        this.setLayout(new BorderLayout());

        // Försöker ladda en bild som heter "Start.png" från samma mapp som klassen ligger i.
        JLabel image;
        try {
            image = new JLabel(new ImageIcon(this.getClass().getResource("Start.png")));
        } catch (Exception e) {
            // Om bilden inte hittas visas istället en text.
            image = new JLabel("Startbild inte hittad");
            // Texten centreras horisontellt i JLabel.
            image.setHorizontalAlignment(0);
        }

        // Lägger till bilden (eller texten) i mitten av panelen.
        this.add(image, "Center");
      
        // Skapar en knapp med texten "Börja spela".
        JButton startButton = new JButton("Börja spela");
        
        // Kopplar knappen till ActionListenern som skickades in till konstruktorn.
        // När man klickar på knappen kommer den lyssnaren att köras.
        startButton.addActionListener(onStart);
        
        // Skapar en ny panel för att hålla knappen (placerad i södra delen av layouten).
        JPanel south = new JPanel();
        south.add(startButton);
      
        // Lägger till panelen med knappen längst ner i StartPanel.
        this.add(south, "South");
    }
}
