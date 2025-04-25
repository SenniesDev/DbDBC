import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.Border;

public class App extends JFrame {

    private static final int NUM_SLOTS = 6;
    private static final int NUM_ROWS = 6;
    private static final int SLOT_WIDTH = 100;
    private static final int SLOT_HEIGHT = 100;
    private static final int GAP = 10;

    private static final Border UNSELECTED_BORDER = BorderFactory.createLineBorder(new Color(30, 30, 30), 4, false);
    private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(new Color(200, 200, 200), 5, false); // Change, if it messes with background
    private static final Font BASIC_FONT = new Font("Inter_FXH Bold", Font.PLAIN, 36);

    private JPanel[] slotPanel;
    private JLabel[][] slots;

    // Variable to keep track of the currently selected slot & hovered over slot
    private JLabel selectedSlot = null;
    private JLabel hoveredOverSlot = null;
    //private JLabel hoveredNotOverSlot = null;

    public App() {
        setTitle("Basic Slots");
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

        slotPanel = new JPanel[NUM_ROWS];
        slots = new JLabel[NUM_ROWS][NUM_SLOTS];
        for(int i = 0; i < NUM_ROWS; i++) {
            slotPanel[i] = new JPanel();
            slotPanel[i].setLayout(new FlowLayout(FlowLayout.CENTER, GAP, GAP));
            slotPanel[i].setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
            slotPanel[i].setBackground(new Color(i * 10 + 40, i * 20 + 40, i * 30 + 60));
        }

        for(int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_SLOTS; j++) {
                slots[i][j] = createSlot(i, j);
                slotPanel[i].add(slots[i][j]);
            }
        }
    }

    private void addComponentsToFrame() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        for(int i = 0; i < NUM_ROWS; i++) {
            mainPanel.add(slotPanel[i]);
            if(i < NUM_ROWS - 1) {
                mainPanel.add(Box.createRigidArea(new Dimension(0, -5)));
            }
        }
        mainPanel.setBackground(new Color(40, 40, 40));
        add(mainPanel, BorderLayout.CENTER);
    }

    private JLabel createSlot(int row, int column) {
        JLabel slot = new JLabel();
        slot.setPreferredSize(new Dimension(SLOT_WIDTH, SLOT_HEIGHT));
        // Start with the unselected border
        slot.setBorder(UNSELECTED_BORDER);
        slot.setOpaque(true); // For background color to show reliably
        slot.setBackground(Color.DARK_GRAY);
        slot.setForeground(new Color(244, 201, 0)); // Color of text
        slot.setHorizontalAlignment(SwingConstants.CENTER);
        slot.setVerticalAlignment(SwingConstants.CENTER);
        slot.setText(String.valueOf(row * NUM_SLOTS + column + 1)); // Sets the value of the text
        //slot.setText(String.valueOf(row + " - " + column)); // Sets the value of the text
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

        int colCounter = 0;
        if (clickedSlot == selectedSlot) {
            selectedSlot = null;
            clickedSlot.setBorder(UNSELECTED_BORDER);
           
            //
            System.out.printf("Slot %s UNSELECTED!\n", clickedSlot.getText());
            //return;
        }

        if (selectedSlot != null) {
            selectedSlot.setBorder(UNSELECTED_BORDER); // If another slot is selected, we unselect the previous slot
        }

        clickedSlot.setBorder(SELECTED_BORDER);
        selectedSlot = clickedSlot;
        System.out.printf("Slot %s SELECTED!\n", clickedSlot.getText());
        //System.out.printf("Slot selected: %s\n", selectedSlot.getText()); // Optional

        // Random
        for(int i = 0; i < randInt(16, NUM_ROWS*NUM_SLOTS); i++) {
            int ranRow = randInt(0, NUM_ROWS-1);
            int ranCol = randInt(0, NUM_SLOTS-1);
            int slotValue = Integer.parseInt(slots[ranRow][ranCol].getText());
            slotValue += randInt(0, NUM_ROWS * NUM_SLOTS);
            slots[ranRow][ranCol].setText(String.valueOf(slotValue));
            // Random color of background
            Color ranColor = slots[ranRow][ranCol].getBackground();
            int[] colArray = {ranColor.getRed(), ranColor.getGreen(), ranColor.getBlue()};
            for(int j = 0; j < colArray.length; j++) {
                colArray[j] += randInt(-25, 25); // Will break if below 0 / above 255
                System.out.printf(" %d,", colArray[j]);
            }
            System.out.printf(" - SLOT %s\n\n", slots[ranRow][ranCol].getText());

            int specColorCounter = 0;

            for(int j = 0; j < 3; j++) {
                if(colArray[j] > 200 && colArray[j] < 256) {
                    specColorCounter++;
               }
            }

            if(specColorCounter >= 2) {
                colCounter++;
            }

            if(colCounter > 5) {
                for(int k = 0; k < NUM_ROWS; k++) {
                    for(int l = 0; l < NUM_SLOTS; l++) {
                        slots[k][l].setText(" WINNER ");
                        slots[k][l].setFont(new Font("Times New Roman", Font.BOLD, 20));
                        slots[k][l].setBackground(new Color(k*5 + 100 % 256, l*5 + 100 % 256, k*l/2 + 100 % 256));
                    }
                }
            }

            try {
                slots[ranRow][ranCol].setBackground(new Color(colArray[0], colArray[1], colArray[2]));
            } catch (Exception e) {
                for(int k = 0; k < 3; k++) {
                    if(colArray[k] < 0 || colArray[k] > 255) {
                        System.out.printf("\n\n ERROR OCCURED - %d OUT OF BOUNDS [0, 255]\n\n", colArray[k]);
                        colArray[k] = 127;
                    }
                }
                slots[ranRow][ranCol].setBackground(new Color(colArray[0], colArray[1], colArray[2]));
                System.out.printf("\n ERROR WAS CORRECTED!!! [%d, %d, %d]\n", colArray[0], colArray[1], colArray[2]);
            }
        }
    }

    private void handleSlotEntered(JLabel enteredSlot) {
        /*if(enteredSlot == hoveredOverSlot) { // If handleSlotExited removed, then uncomment
            return;
        }*/

        if(hoveredOverSlot != null) {
            hoveredOverSlot.setBackground(Color.DARK_GRAY);
        }

        //enteredSlot.setFont(new Font("Arial", Font.PLAIN, 40));
        enteredSlot.setBackground(new Color(80, 80, 80));
        System.out.printf("Slot %s HOVERED ON!\n", enteredSlot.getText());

        hoveredOverSlot = enteredSlot;
    }

    private void handleSlotExited(JLabel exitedSlot) {
        exitedSlot.setBackground(Color.DARK_GRAY);
        System.out.printf("Slot %s NOT HOVERED OVER!\n", exitedSlot.getText());

        //hoveredNotOverSlot = exitedSlot;
    }

    private int randInt(int min, int max) {
        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}
