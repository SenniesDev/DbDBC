import javax.swing.*;
import java.awt.*;

public class App extends JFrame {

    private static final int NUM_SLOTS = 4;
    private static final int SLOT_WIDTH = 80;
    private static final int SLOT_HEIGHT = 80;
    private static final int GAP = 10;

    private JPanel slotPanel;
    private JLabel[] slots;

    public App() {
        setTitle("Slot Row Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setSize(600, 600);
        setLocationRelativeTo(null);
        //setLayout(new FlowLayout(FlowLayout.CENTER, GAP, GAP));
        setLayout(new BorderLayout()); // 2nd iteration

        initializeComponents();
        addComponentsToFrame();

        pack(); // Adjust frame size to fit components
        setMinimumSize(getSize()); // Prevent resizing than packed size
        setVisible(true);
    }

    private void initializeComponents() {
        slotPanel = new JPanel();
        slotPanel.setLayout(new FlowLayout(FlowLayout.CENTER, GAP, GAP));

        slots = new JLabel[NUM_SLOTS];
        for (int i = 0; i < NUM_SLOTS; i++) {
            slots[i] = createSlot(i);
            slotPanel.add(slots[i]);
        }
    }

    private void addComponentsToFrame() {

        add(slotPanel);
    }

    private JLabel createSlot(int index) {
        JLabel slot = new JLabel();
        slot.setPreferredSize(new Dimension(SLOT_WIDTH, SLOT_HEIGHT));
        slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
        slot.setOpaque(true); // For background color to show reliably
        slot.setBackground(Color.DARK_GRAY);
        slot.setForeground(new Color(244, 201,0)); // Color of text
        slot.setHorizontalAlignment(SwingConstants.CENTER);
        slot.setVerticalAlignment(SwingConstants.CENTER);
        slot.setText(String.valueOf(index)); // Sets the value of the text
        slot.setFont(new Font("Inter_FXH Bold", Font.PLAIN, 20));
        return slot;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App());
    }
}
