import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StartPanel extends JPanel {
    public StartPanel(ActionListener onStart) {
        this.setLayout(new BorderLayout());

        JLabel image;
        try {
            image = new JLabel(new ImageIcon(this.getClass().getResource("Start.png")));
        } catch (Exception var5) {
            image = new JLabel("Startbild inte hittad");
            image.setHorizontalAlignment(0);
        }

        this.add(image, "Center");
        JButton startButton = new JButton("Börja spela");
        startButton.addActionListener(onStart);
        JPanel south = new JPanel();
        south.add(startButton);
        this.add(south, "South");
    }
}
