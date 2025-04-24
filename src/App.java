import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.Border;

public class App extends JFrame {

    private static final int NUM_SLOTS = 300;
    private static final int SLOT_WIDTH = 80;
    private static final int SLOT_HEIGHT = 80;
    private static final int GAP = 10;

    private static final Border UNSELECTED_BORDER = BorderFactory.createLineBorder(new Color(30, 30, 30), 3, false);
    private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(new Color(80, 80, 80), 3, false); // Change, if it messes with background
    private static final Font BASIC_FONT = new Font("Inter_FXH Bold", Font.PLAIN, 20);

    private JPanel slotPanel;
    private JLabel[] slots;

    // Variable to keep track of the currently selected slot & hovered over slot
    private JLabel selectedSlot = null;
    private JLabel hoveredOverSlot = null;
    //private JLabel hoveredNotOverSlot = null;

    public App() {
        setTitle("Slot Row Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
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
        slotPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));

        slots = new JLabel[NUM_SLOTS];
        for (int i = 0; i < NUM_SLOTS; i++) {
            slots[i] = createSlot(i);
            slotPanel.add(slots[i]);
        }
    }

    private void addComponentsToFrame() {
        add(slotPanel, BorderLayout.CENTER);
    }

    private JLabel createSlot(int index) {
        JLabel slot = new JLabel();
        slot.setPreferredSize(new Dimension(SLOT_WIDTH, SLOT_HEIGHT));
        // Start with the unselected border
        slot.setBorder(UNSELECTED_BORDER);
        slot.setOpaque(true); // For background color to show reliably
        slot.setBackground(Color.DARK_GRAY);
        slot.setForeground(new Color(244, 201,0)); // Color of text
        slot.setHorizontalAlignment(SwingConstants.CENTER);
        slot.setVerticalAlignment(SwingConstants.CENTER);
        slot.setText(String.valueOf(index)); // Sets the value of the text
        slot.setFont(BASIC_FONT);

        // Add a MouseListener to handle clicks
        slot.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                handleSlotClick(slot); // Pass the clicked slot to the handler
            }
        });

        slot.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                handleSlotEntered(slot);
            }
        });

        slot.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent e) {
                handleSlotExited(slot);
            }
        });

        return slot;
    }

    // Method to handle the logic when a slot is clicked
    private void handleSlotClick(JLabel clickedSlot) {

        if (clickedSlot == selectedSlot) {
            return;
        }

        if (selectedSlot != null) {
            selectedSlot.setBorder(UNSELECTED_BORDER); // If another slot is selected, we unselect the previous slot
        }

        clickedSlot.setBorder(SELECTED_BORDER);
        selectedSlot = clickedSlot;

        //System.out.printf("Slot selected: %s\n", selectedSlot.getText()); // Optional
    }

    private void handleSlotEntered(JLabel enteredSlot) {
        /*if(enteredSlot == hoveredOverSlot) { // If handleSlotExited removed, then uncomment
            return;
        }*/

        if(hoveredOverSlot != null) {
            hoveredOverSlot.setBackground(Color.DARK_GRAY);
        }

        //enteredSlot.setFont(new Font("Arial", Font.PLAIN, 40));
        enteredSlot.setBackground(new Color(50, 50, 50));
        System.out.printf("\nSlot %s hovered over!\n", enteredSlot.getText());

        hoveredOverSlot = enteredSlot;
    }

    private void handleSlotExited(JLabel exitedSlot) {
        exitedSlot.setBackground(Color.DARK_GRAY);
        System.out.printf("\nSlot %s not being hovered over anymore!\n", exitedSlot.getText());

        //hoveredNotOverSlot = exitedSlot;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}
