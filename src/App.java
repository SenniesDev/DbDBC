import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.Timer;
import java.util.TimerTask;

public class App extends JFrame {

    private static final int NUM_SLOTS = 4;
    private static final int NUM_ROWS = 4;
    private static final int SLOT_WIDTH = 100;
    private static final int SLOT_HEIGHT = 100;
    private static final int GAP = 10;
    private static final int SIDEPANEL_WIDTH = 300;
    private static final String SIDEPANEL_DEFAULT_NAME = "Label";

    private static final Color COLOR_DARKEST = new Color (52, 78, 65);
    private static final Color COLOR_DARK = new Color (58, 90, 64);
    private static final Color COLOR_AVERAGE = new Color (88, 129, 87);
    private static final Color COLOR_BRIGHT = new Color (163, 177, 138);
    private static final Color COLOR_BRIGHTEST = new Color (218, 215, 205);

    //private static final Border UNSELECTED_BORDER = BorderFactory.createLineBorder(new Color(30, 30, 30), 2, false);
    private static final Border UNSELECTED_BORDER = BorderFactory.createLineBorder(COLOR_AVERAGE, 2, false);
    // private static final Border HOVERED_BORDER = BorderFactory.createLineBorder(new Color(100, 100, 100), 3, false);
    private static final Border HOVERED_BORDER = BorderFactory.createLineBorder(COLOR_BRIGHT, 3, false);
    //private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(new Color(180, 180, 180), 5, false); // Change, if it messes with background
    private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(COLOR_BRIGHTEST, 5, false); // Change, if it messes with background

    private static final Font BASIC_FONT = new Font("Inter_FXH Bold", Font.PLAIN, (int) Math.round(Math.sqrt(SLOT_WIDTH*SLOT_HEIGHT*5))/5);

    //private static final Font SIDE_FONT = new Font("Inter_FXH Bold", Font.PLAIN, 24);

    private JPanel sidePanel;
    private JPanel mainContentArea;
    private JPanel bottomMainPanelWrapper;
    private JPanel bottomMiddleMainPanelWrapper;
    private JPanel perkPanel;
    private JPanel temporaryFarRight;

    private ArrayList<JLabel> sideLabelList;
    private ArrayList<Component> sideLabelListRigidAreas;
    private JPanel[] perkSubPanel;
    private JLabel[][] slots;
    private JLabel selectedSlot = null;     // Variable to keep track of the currently selected slot & hovered over slot

    private Timer singleClickTimer;
    private JLabel lastClickedLabel = null;     // Tracks which label was clicked last

    public App() {
        setTitle("App window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeComponents();
        addComponentsToFrame();

        pack(); // Adjust frame size to fit components
        setLocationRelativeTo(null);
        setMinimumSize(getSize()); // Prevent resizing than packed size
        setMaximumSize(getSize());
        setVisible(true);
    }

    private void initializeComponents() {

        sideLabelList = new ArrayList<>();
        sideLabelListRigidAreas = new ArrayList<>();

        sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, getHeight()));

        sidePanel.setBackground(COLOR_DARKEST);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(GAP,GAP,GAP,GAP));

        JLabel initialLabel = createSideLabel(true); // Creates original "Create new label" label
        sidePanel.add(initialLabel);

        mainContentArea = new JPanel();
        mainContentArea.setBackground(Color.white);
        mainContentArea.setLayout(new BorderLayout());

        bottomMainPanelWrapper = new JPanel();
        bottomMainPanelWrapper.setBackground(Color.yellow);
        bottomMainPanelWrapper.setLayout(new BorderLayout());

        // Creates a wrapper for the inside of the bottom main panel (currently stretches over the entirety of bottomMainPanel)
        bottomMiddleMainPanelWrapper = new JPanel();
        bottomMiddleMainPanelWrapper.setBackground(Color.blue);
        bottomMiddleMainPanelWrapper.setLayout(new BorderLayout());

        // Creates a (currently invisible) panel on the far right of bottomMiddleMainPanelWrapper
        temporaryFarRight = new JPanel();
        temporaryFarRight.setBackground(Color.green);
        temporaryFarRight.setLayout(new BorderLayout());

        perkPanel = new JPanel();
        perkPanel.setBackground(COLOR_DARKEST);
        perkPanel.setLayout(new BoxLayout(perkPanel, BoxLayout.Y_AXIS));

        perkSubPanel = new JPanel[NUM_ROWS];
        slots = new JLabel[NUM_ROWS][NUM_SLOTS];
        for(int i = 0; i < NUM_ROWS; i++) {
            perkSubPanel[i] = new JPanel();
            perkSubPanel[i].setLayout(new FlowLayout(FlowLayout.CENTER, GAP, GAP));
            perkSubPanel[i].setBorder(BorderFactory.createEmptyBorder());
            perkSubPanel[i].setBackground(COLOR_DARKEST);
        }

        for(int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_SLOTS; j++) {
                slots[i][j] = createSlot(i, j, "Perk");
                perkSubPanel[i].add(slots[i][j]);
            }
        }
    }

    private void addComponentsToFrame() {

        add(this.sidePanel, BorderLayout.WEST);
        add(mainContentArea, BorderLayout.CENTER);
        mainContentArea.add(bottomMainPanelWrapper, BorderLayout.SOUTH);
        bottomMainPanelWrapper.add(perkPanel, BorderLayout.WEST);
        bottomMainPanelWrapper.add(bottomMiddleMainPanelWrapper, BorderLayout.CENTER);
        bottomMainPanelWrapper.add(temporaryFarRight, BorderLayout.EAST);

        for(int i = 0; i < NUM_ROWS; i++) {
            perkPanel.add(perkSubPanel[i]);
            if(i < NUM_ROWS - 1) {
                perkPanel.add(Box.createRigidArea(new Dimension(0, GAP/2)));
            }
        }
    }

    private void openSettings(JLabel label) {
        JDialog settings = new JDialog(this, "Settings", true); // Modal?
        settings.setLayout(new BorderLayout());

        //The Settings
        JLabel settings1Label = new JLabel("Name");
        settings1Label.setFont(new Font("Inter_FXH", Font.PLAIN, 20));
        settings1Label.setForeground(Color.WHITE);

        JTextField settings1TextField = new JTextField(15);
        settings1TextField.setPreferredSize(new Dimension(150, 25));
        settings1TextField.setFont(new Font("Inter_FXH", Font.PLAIN, 18));
        settings1TextField.setText(label.getText());
        settings1TextField.setBackground(new Color(230,230,230));
        settings1TextField.setForeground(new Color(10,10,10));

        JLabel settings2Label = new JLabel("Delete? (test)");
        settings2Label.setFont(new Font("Inter_FXH", Font.PLAIN, 20));
        settings2Label.setForeground(Color.WHITE);

        JCheckBox settings2Checkbox = new JCheckBox("Enable feature");
        settings2Checkbox.setPreferredSize(new Dimension(120, 25));
        settings2Checkbox.setFont(new Font("Inter_FXH", Font.PLAIN, 18));
        settings2Checkbox.setSelected(false);
        settings2Checkbox.setBackground(COLOR_DARK);
        settings2Checkbox.setForeground(Color.WHITE);

        // Adding all Settings components to the panel
        JPanel settingsPanel = new JPanel(new GridLayout(0,2,5,5));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(GAP,GAP,GAP,GAP));
        settingsPanel.add(settings1Label);
        settingsPanel.add(settings1TextField);
        settingsPanel.add(settings2Label);
        settingsPanel.add(settings2Checkbox);
        settingsPanel.setBackground(COLOR_DARK);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String settings1Value = settings1TextField.getText();
                boolean setting2Enabled = settings2Checkbox.isSelected();
                label.setText(settings1Value);
                if(setting2Enabled) {
                    removeSideLabel(label);
                }
                settings.dispose();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings.dispose();
            }
        });

        // Adding both panels (with settings and buttons (Cancel / Save) to Settings)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBackground(COLOR_DARK);

        settings.add(settingsPanel, BorderLayout.CENTER);
        settings.add(buttonPanel, BorderLayout.SOUTH);

        settings.pack();
        settings.setLocationRelativeTo(this);
        settings.setVisible(true);
    }

    private JLabel createSideLabel(boolean isCreator) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(SIDEPANEL_WIDTH - 2*GAP, 24 + GAP*2));
        label.setMaximumSize(new Dimension(SIDEPANEL_WIDTH - 2*GAP, 24 + GAP*2));
        //label.setBorder(UNSELECTED_BORDER);
        label.setOpaque(true);
        sideLabelList.add(label);
        int index = sideLabelList.indexOf(label);
        label.setBorder(BorderFactory.createLineBorder(COLOR_AVERAGE, 2, true));

        if(isCreator) {
            label.setText("Create new " + SIDEPANEL_DEFAULT_NAME);
            label.setFont(new Font("Inter_FXH", Font.PLAIN, 24));
        } else {
            label.setText(SIDEPANEL_DEFAULT_NAME + " " + sideLabelList.indexOf(label)); // should set the text of current index
            label.setFont(new Font("Inter_FXH", Font.PLAIN, 20));
            sideLabelListRigidAreas.add(Box.createRigidArea(new Dimension(0, GAP/2)));
            sidePanel.add(sideLabelListRigidAreas.getLast());
        }
        Color labelColor = new Color(COLOR_DARK.getRed() + index * 2, COLOR_DARK.getGreen(), COLOR_DARK.getBlue() + index * 5);
        label.setBackground(labelColor);
        label.setForeground(new Color(230,230,230));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        sidePanel.add(label);

        System.out.print("[ ");
        int i = 0;
        while(i < sideLabelList.size()) {
            System.out.printf("%s", sideLabelList.get(i).getText());
            i++;
            if(i < sideLabelList.size()) {
                System.out.print(", ");
            }
        }
        System.out.print(" ]\n");

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { // could use a flag/delay approach instead
                if(isCreator) {
                    //sideLabelList.add(createSideLabel(false));
                    createSideLabel(false);
                    sidePanel.revalidate();
                    sidePanel.repaint(); // updates the entire table
                } else {
                    if(e.getClickCount() == 1) {
                        singleClickTimer = new Timer();
                        lastClickedLabel = label;
                        singleClickTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if(lastClickedLabel == label) {
                                    System.out.print("\nSingle click\n"); // Should switch to that current build
                                    lastClickedLabel = null;
                                }
                            }
                        }, 400); // in milliseconds
                    } else if (e.getClickCount() == 2) {
                        if(singleClickTimer != null) {
                                singleClickTimer.cancel();
                                singleClickTimer = null;
                                lastClickedLabel = null;
                            System.out.print("Double click\n");
                            openSettings(label);
                        }
                    }
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setBackground(labelColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                label.setBackground(labelColor);
            }
        });

        return label;
    }

    private void removeSideLabel(JLabel label) {
        sidePanel.remove(sideLabelListRigidAreas.get(sideLabelList.indexOf(label)-1));
        sideLabelListRigidAreas.remove(sideLabelList.indexOf(label)-1);
        int index = sideLabelList.indexOf(label);
        while(index < sideLabelList.size()) {
            if(sideLabelList.get(index).getText().contains(SIDEPANEL_DEFAULT_NAME + " " + index)) {
                sideLabelList.get(index).setText(SIDEPANEL_DEFAULT_NAME + " " + (index-1));
                //System.out.printf("Label[%d] was color (%d, %d, %d)\n", index-1, sideLabelList.get(index).getBackground().getRed(), sideLabelList.get(index).getBackground().getGreen(), sideLabelList.get(index).getBackground().getBlue());
                Color labelColor = new Color(COLOR_DARK.getRed() + (index-1) * 2, COLOR_DARK.getGreen(), COLOR_DARK.getBlue() + (index-1) * 5);
                sideLabelList.get(index).setBackground(labelColor);
                //System.out.printf("Label[%d] is color (%d, %d, %d)\n", index-1, labelColor.getRed(), labelColor.getGreen(), labelColor.getBlue());
            }
            index++;
        }
        sidePanel.remove(label);
        sideLabelList.remove(label);
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    private JLabel createSlot(int row, int column, String type) {
        JLabel slot = new JLabel();
        slot.setPreferredSize(new Dimension(SLOT_WIDTH, SLOT_HEIGHT));
        slot.setBorder(UNSELECTED_BORDER); // Start with the unselected border
        slot.setOpaque(true); // For background color to show reliably
        if(type == "Perk") {
            slot.setText(String.valueOf(row * NUM_SLOTS + column + 1)); // Sets the value of the text
        } else {
            slot.setText("0");
        }
        slot.setFont(BASIC_FONT);
        slot.setBackground(COLOR_DARK);
        slot.setForeground(Color.WHITE); // Color of text
        slot.setHorizontalAlignment(SwingConstants.CENTER);
        slot.setVerticalAlignment(SwingConstants.CENTER);

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
            selectedSlot = null;
            clickedSlot.setBorder(UNSELECTED_BORDER);
        }
        if (selectedSlot != null) {
            selectedSlot.setBorder(UNSELECTED_BORDER); // If another slot is selected, we unselect the previous slot
        }
        clickedSlot.setBorder(SELECTED_BORDER);
        selectedSlot = clickedSlot;

    }

    private void handleSlotEntered(JLabel enteredSlot) {

        if(enteredSlot != selectedSlot) {
            enteredSlot.setBorder(HOVERED_BORDER);
        }
        enteredSlot.setBackground(COLOR_DARK.brighter());
    }

    private void handleSlotExited(JLabel exitedSlot) {

        if(exitedSlot != selectedSlot) {
            exitedSlot.setBorder(UNSELECTED_BORDER);
        }
        exitedSlot.setBackground(COLOR_DARK);
    }

    // ------------------------------------------------------

    private int randInt(int min, int max) {
        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}