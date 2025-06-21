import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Timer;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;


public class App extends JFrame {

    private static final int[] NUM_BUILD = {1, 4, 1, 2, 1, 1, 1}; // surv icon, surv perks, surv items, surv addons, offering, map, killer
    private static final int NUM_ROWS = 4;
    private static final int SLOT_WIDTH = 100;
    private static final int SLOT_HEIGHT = 100;
    private static final int GAP = 5;
    private static final int SIDEPANEL_WIDTH = 240;
    private static final String SIDEPANEL_DEFAULT_NAME = "Label";
    private static final String[] DEFAULT_TEXT = {"s_icon", "s_perk", "s_item", "s_addon", "s_off", "map", "k_icon", "k_perk", "k_addon", "k_off"};
    private static final String DBD_ABS_PATH = "C:/Program Files (x86)/Steam/steamapps/common/Dead by Daylight/DeadByDaylight/Content/UI/Icons/";
    private static final String[] DBD_REL_PATHS = {"CharPortraits", "Perks", "Items", "ItemAddons", "Favors", "Actions", "CharPortraits", "Powers", "ItemAddons", "Favors"}; // maps are currently disabled

    private static final Color COLOR_DARKEST = new Color(52, 78, 65);
    private static final Color COLOR_DARK = new Color(58, 90, 64);
    private static final Color COLOR_AVERAGE = new Color(88, 129, 87);
    private static final Color COLOR_BRIGHT = new Color(163, 177, 138);
    private static final Color COLOR_BRIGHTEST = new Color(218, 215, 205);

    private static final Border UNSELECTED_BORDER = BorderFactory.createLineBorder(COLOR_AVERAGE, 2, false);
    private static final Border HOVERED_BORDER = BorderFactory.createLineBorder(COLOR_BRIGHT, 3, false);
    private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(COLOR_BRIGHTEST, 4, false); // Change, if it messes with background
    private static final Border WINDOW_BORDER = BorderFactory.createLineBorder(COLOR_DARKEST, 3, false); // Change, if it messes with background

    private static final Font BASIC_FONT = new Font("Inter_FXH", Font.PLAIN, (int) Math.round(Math.sqrt(SLOT_WIDTH * SLOT_HEIGHT * 5)) / 5);

    private JPanel leftPanel;
    private JPanel mainWrap;

    private JScrollPane selectionWrap;

    private JPanel mainBotWrap;
    private JPanel mainBotMidWrap;

    private JPanel buildWrap;
    private ArrayList<ArrayList<JLabel>> buildTableOfSlots; // Change this later on to a fixed Array length!

    private JPanel botRightWrap;
    private JPanel[] botRightRow;
    private JLabel[] botRightSlots;

    private ArrayList<JLabel> leftItemList;
    private ArrayList<Component> leftGapList;

    private ArrayList<ArrayList<slotItem>> selectionItems;
    private ArrayList<JPanel> selectionPanels;
    private ArrayList<ArrayList<JLabel>> selectionLabels;

    private JPanel[] buildRow;

    private JLabel[] selectedSlots = {null, null}; // Keeps track of above defined things, 1st is build, 2nd is selection
    private int[] selectedType = {-1, -1};

    private Timer singleClickTimer;
    private JLabel lastClickedLabel = null;     // Tracks which label was clicked last

    private double currentMouseX; // For future use
    private double currentMouseY; // For future use

    public App() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screenDevices = ge.getScreenDevices(); // For future use, multiple monitor display compatibility
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle screenBounds = defaultScreen.getDefaultConfiguration().getBounds();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(defaultScreen.getDefaultConfiguration());

        int x = screenBounds.x + screenInsets.left;
        int y = screenBounds.y + screenInsets.top;
        int width = screenBounds.width - screenInsets.left - screenInsets.right;
        int height = screenBounds.height - screenInsets.top - screenInsets.bottom;

        setBounds(x, y, width, height);
        initializeComponents();
        addComponentsToFrame();
        setVisible(true);

        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
    }

    private void initializeComponents() {

        leftItemList = new ArrayList<>();
        leftGapList = new ArrayList<>();

        leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(SIDEPANEL_WIDTH, getHeight()));

        leftPanel.setBackground(COLOR_DARKEST);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));

        JLabel initialLabel = createSideLabel(true); // Creates original "Create new label" label
        leftPanel.add(initialLabel);

        // Main

        mainWrap = new JPanel();
        mainWrap.setBackground(Color.BLACK);
        mainWrap.setLayout(new BorderLayout());

        // Bottom wrapper for perk build, map, killer
        mainBotWrap = new JPanel();
        mainBotWrap.setBackground(Color.yellow);
        mainBotWrap.setLayout(new BorderLayout());

        // Creates a wrapper for the inside of the bottom main panel (currently stretches over the entirety of bottomMainPanel)
        mainBotMidWrap = new JPanel();
        mainBotMidWrap.setBackground(Color.blue);
        mainBotMidWrap.setLayout(new BorderLayout());

        // Selection

        selectionWrap = new JScrollPane();
        selectionWrap.setBackground(Color.ORANGE);
        selectionWrap.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        selectionWrap.getVerticalScrollBar().setUnitIncrement(20);

        selectionItems = new ArrayList<ArrayList<slotItem>>(); // Array of Arrays of Item types
        selectionPanels = new ArrayList<JPanel>(); // Array of Panels in Selection (OF TYPES)
        selectionLabels = new ArrayList<ArrayList<JLabel>>(); // For future editing

        // Build Wrapper

        buildWrap = new JPanel();
        buildWrap.setBackground(COLOR_DARKEST);
        buildWrap.setLayout(new BoxLayout(buildWrap, BoxLayout.Y_AXIS));

        buildRow = new JPanel[NUM_ROWS]; // Row panel
        buildTableOfSlots = new ArrayList<ArrayList<JLabel>>();

        botRightWrap = new JPanel();
        botRightWrap.setLayout(new BoxLayout(botRightWrap, BoxLayout.Y_AXIS));

        botRightRow = new JPanel[2];
        botRightSlots = new JLabel[botRightRow.length];

        // Just building out rows, so we can enter in blank slots
        for(int i = 0; i < NUM_ROWS; i++) {
            buildRow[i] = new JPanel();
            buildTableOfSlots.add(new ArrayList<>());
        }

        // Adds icons on bottom right (map, killer)
        for(int i = 0; i < botRightRow.length; i++) {
            botRightRow[i] = new JPanel();

            JPanel currentRow = botRightRow[i];
            currentRow.setLayout(new FlowLayout(FlowLayout.CENTER, GAP, GAP));
            currentRow.setBorder(BorderFactory.createEmptyBorder());
            currentRow.setBackground(COLOR_DARKEST);

            botRightSlots[i] = createSlot(i, 0, i+5, 0); // Creates new slot
            currentRow.add(botRightSlots[i]); // Adds that slot to single row
            botRightWrap.add(currentRow);
        }

        for (int typeIx = 0; typeIx < NUM_BUILD.length; typeIx++) {

            selectionItems.add(new ArrayList<>()); // adds a new ArrayList for storing all items
            selectionPanels.add(new JPanel()); // adds a new TOP panel for each item type
            selectionLabels.add(new ArrayList<>());

            // Selection Panel Setup

            JPanel currentPanel = selectionPanels.get(typeIx);
            currentPanel.setBorder(WINDOW_BORDER);
            currentPanel.setBackground(COLOR_DARK);
            currentPanel.setLayout(new WrapLayout());

            // Looks through files, return how many "files" are of a certain type,

            int itemQuantity = fileSearch(typeIx);

            for(int itemIx = 0; itemIx < itemQuantity; itemIx++) {
                selectionLabels.get(typeIx).add(createSlot(0, itemIx, typeIx, 1));
                currentPanel.add(selectionLabels.get(typeIx).getLast()); // We can remove a label through the array
            }

            switch(typeIx) {
                case 5, 6:
                    break;
                default:
                    for(int j = 0; j < NUM_BUILD[typeIx]; j++) { // for each element's quantity, we create that amount of slots
                        for (int k = 0; k < NUM_ROWS; k++) { // adds rows
                            buildTableOfSlots.get(k).add(createSlot(0, 0, typeIx, 0)); // Creates new slot
                            buildRow[k].add(buildTableOfSlots.get(k).getLast()); // adds it to build
                            buildRow[k].setLayout(new FlowLayout(FlowLayout.CENTER, GAP * 2, GAP));
                            buildRow[k].setBorder(BorderFactory.createEmptyBorder());
                            buildRow[k].setBackground(COLOR_DARKEST);
                        }
                    }
            }
        }

        for(int i = 0; i < NUM_ROWS; i++) {
            buildWrap.add(buildRow[i]);
            if(i < NUM_ROWS - 1) {
                buildWrap.add(Box.createRigidArea(new Dimension(0, GAP/2)));
            }
        }
        selectionPanels.add(new JPanel());
        JLabel blankLabel = new JLabel();
        blankLabel.setText("PLEASE SELECT A PERK!");
        blankLabel.setFont(new Font("Inter_FXH Bold", Font.BOLD, 72));
        blankLabel.setBackground(Color.BLACK);
        selectionPanels.getLast().add(blankLabel);
    }

    // !!! HERE !!!

    private void addComponentsToFrame() {

        add(this.leftPanel, BorderLayout.WEST);
        add(mainWrap, BorderLayout.CENTER);
        mainWrap.add(mainBotWrap, BorderLayout.SOUTH);
        mainWrap.add(selectionWrap);
        selectionWrap.setViewportView(selectionPanels.getLast()); // Default view is BLANK
        mainBotWrap.add(buildWrap, BorderLayout.WEST);
        mainBotWrap.add(mainBotMidWrap, BorderLayout.CENTER);
        mainBotWrap.add(botRightWrap, BorderLayout.EAST);
    }




    // Settings for left-side labels

    private void openSettings(JLabel label) {
        JDialog settings = new JDialog(this, "Settings", true); // Modal?
        settings.setLayout(new BorderLayout());

        JLabel setting_a = new JLabel("Name");
        setting_a.setFont(new Font("Inter_FXH", Font.PLAIN, 20));
        setting_a.setForeground(Color.WHITE);

        JTextField setting_a_text = new JTextField(15);
        setting_a_text.setPreferredSize(new Dimension(150, 25));
        setting_a_text.setFont(new Font("Inter_FXH", Font.PLAIN, 18));
        setting_a_text.setText(label.getText());
        setting_a_text.setBackground(new Color(230,230,230));
        setting_a_text.setForeground(new Color(10,10,10));

        JLabel setting_b = new JLabel("Delete \"" + label.getText() + "\"?");
        setting_b.setFont(new Font("Inter_FXH", Font.PLAIN, 20));
        setting_b.setForeground(Color.WHITE);

        JCheckBox setting_b_checkbox = new JCheckBox("Yes");
        setting_b_checkbox.setPreferredSize(new Dimension(120, 25));
        setting_b_checkbox.setFont(new Font("Inter_FXH", Font.PLAIN, 18));
        setting_b_checkbox.setSelected(false);
        setting_b_checkbox.setBackground(COLOR_DARK);
        setting_b_checkbox.setForeground(Color.WHITE);

        // Adding all Settings components to the panel
        JPanel settingsPanel = new JPanel(new GridLayout(0,2,5,5));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(GAP,GAP,GAP,GAP));
        settingsPanel.add(setting_a);
        settingsPanel.add(setting_a_text);
        settingsPanel.add(setting_b);
        settingsPanel.add(setting_b_checkbox);
        settingsPanel.setBackground(COLOR_DARK);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            saveSettings(label, setting_a_text, setting_b_checkbox);
            settings.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> settings.dispose());

        // Adding both panels (with settings and buttons (Cancel / Save) to Settings)
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(saveButton);
        buttons.add(cancelButton);
        buttons.setBackground(COLOR_DARK);

        settings.add(settingsPanel, BorderLayout.CENTER);
        settings.add(buttons, BorderLayout.SOUTH);

        settings.pack();
        settings.setLocationRelativeTo(this);
        settings.setVisible(true);
    }

    private void saveSettings(JLabel label, JTextField settings1TextField, JCheckBox settings2Checkbox) {
        String settings1Value = settings1TextField.getText();
        boolean setting2Enabled = settings2Checkbox.isSelected();
        label.setText(settings1Value);
        if(setting2Enabled) {
            removeSideLabel(label);
        }
    }




    // Sidelabels

    private JLabel createSideLabel(boolean isCreator) {
        JLabel label = new JLabel();

        label.setPreferredSize(new Dimension(SIDEPANEL_WIDTH - 2*GAP, 24 + GAP*2));
        label.setMaximumSize(new Dimension(SIDEPANEL_WIDTH - 2*GAP, 24 + GAP*2));
        label.setOpaque(true);

        leftItemList.add(label);
        int index = leftItemList.indexOf(label);
        label.setBorder(BorderFactory.createLineBorder(COLOR_AVERAGE, 2, true));

        if(isCreator) {
            label.setText("Create new " + SIDEPANEL_DEFAULT_NAME);
            label.setFont(new Font("Inter_FXH", Font.PLAIN, 24));
        } else {
            label.setText(SIDEPANEL_DEFAULT_NAME + " " + leftItemList.indexOf(label)); // should set the text of current index
            label.setFont(new Font("Inter_FXH", Font.PLAIN, 20));
            leftGapList.add(Box.createRigidArea(new Dimension(0, GAP/2)));
            leftPanel.add(leftGapList.get(leftItemList.indexOf(label)-1));
        }
        Color labelColor = new Color(COLOR_DARK.getRed() + index * 2, COLOR_DARK.getGreen(), COLOR_DARK.getBlue() + index * 5);
        label.setBackground(labelColor);
        label.setForeground(new Color(230,230,230));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        leftPanel.add(label);

        System.out.print("[");
        int i = 0;
        while(i < leftItemList.size()) {
            System.out.printf("%s", leftItemList.get(i).getText());
            i++;
            if(i < leftItemList.size()) {
                System.out.print(", ");
            }
        }
        System.out.print("] \n");

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { // could use a flag/delay approach instead
                if(isCreator) {
                    //sidepanelList.add(createSideLabel(false));
                    createSideLabel(false);
                    leftPanel.revalidate();
                    leftPanel.repaint(); // updates the entire table
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
        leftPanel.remove(leftGapList.get(leftItemList.indexOf(label)-1));
        leftGapList.remove(leftItemList.indexOf(label)-1);
        int index = leftItemList.indexOf(label);
        while(index < leftItemList.size()) {
            if(leftItemList.get(index).getText().contains(SIDEPANEL_DEFAULT_NAME + " " + index)) {
                leftItemList.get(index).setText(SIDEPANEL_DEFAULT_NAME + " " + (index-1));
                Color labelColor = new Color(COLOR_DARK.getRed() + (index-1) * 2, COLOR_DARK.getGreen(), COLOR_DARK.getBlue() + (index-1) * 5);
                leftItemList.get(index).setBackground(labelColor);
            }
            index++;
        }
        leftPanel.remove(label);
        leftItemList.remove(label);
        leftPanel.revalidate();
        leftPanel.repaint();
    }

    // Slots

    private JLabel createSlot(int row, int column, int type, int isSelection) {
        JLabel slot = new JLabel();

        if(type == 5 || type == 6) {
            slot.setPreferredSize(new Dimension(SLOT_WIDTH*2, SLOT_HEIGHT*2));
        } else {
            slot.setPreferredSize(new Dimension(SLOT_WIDTH, SLOT_HEIGHT));
        }
        slot.setBorder(UNSELECTED_BORDER); // Start with the unselected border
        slot.setOpaque(true); // For background color to show reliable

        if (isSelection == 1) {
            slot.setIcon(selectionItems.get(type).get(column).getImageIcon()); // Sets the icon of the slot
        } else {
            if(type < DEFAULT_TEXT.length && type > -1) {
                slot.setText(DEFAULT_TEXT[type]);
            } else {
                slot.setText("OOB");
            }
        }

        int stringWidth = slot.getFontMetrics(BASIC_FONT).stringWidth(slot.getText());
        double widthRatio = (double) SLOT_WIDTH / (double) stringWidth;
        //System.out.printf("[%d][%d] is size of %f - SLOT_WIDTH = %d, stringWidth = %d\n", row, column, widthRatio, SLOT_WIDTH, stringWidth);
        slot.setFont(new Font("Inter_FXH", Font.PLAIN, (int)Math.floor(BASIC_FONT.getSize()*widthRatio)-5));
        Font curFont = slot.getFont();
        //System.out.printf("Start - Font: %s; Size: %d; toString: %s\n", curFont.getFontName(), curFont.getSize(), curFont.toString());

        slot.setBackground(COLOR_DARK);
        slot.setForeground(Color.WHITE); // Color of text
        slot.setHorizontalAlignment(SwingConstants.CENTER);
        slot.setVerticalAlignment(SwingConstants.CENTER);

        // Add a MouseListener to handle clicks
        slot.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                handleSlotClick(slot, type, isSelection); // Pass the clicked slot to the handler
            }
        });

        slot.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                handleSlotEntered(slot, type, isSelection);
            }
        });

        slot.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent e) {
                handleSlotExited(slot, type, isSelection);
            }
        });

        return slot;
    }



    // Handlers

    private void handleSlotClick(JLabel clickedSlot, int type, int isSelection) {

        int prevSelectedType = selectedType[isSelection];

        if(selectedSlots[isSelection] == null) { // When selected slots have not yet been selected / have been unselected

            clickedSlot.setBorder(SELECTED_BORDER);
            selectedSlots[isSelection] = clickedSlot;
            selectedType[isSelection] = type;

        } else if (clickedSlot == selectedSlots[isSelection]) {
            selectedSlots[isSelection] = null;
            selectedType[isSelection] = -1;
            clickedSlot.setBorder(UNSELECTED_BORDER);
            if(isSelection == 0) {
                clickedSlot.setIcon(null);
                clickedSlot.setText(DEFAULT_TEXT[type]);
            }
            return;
        } else if (selectedSlots[isSelection] != null) {
            selectedSlots[isSelection].setBorder(UNSELECTED_BORDER);
            clickedSlot.setBorder(SELECTED_BORDER);
            selectedSlots[isSelection] = clickedSlot;
            selectedType[isSelection] = type;
        } else {
            throw new RuntimeException("Cannot select a slot");
        }

        if(isSelection == 0) {
            if (prevSelectedType == type) {
                return;
            } else {
                selectionWrap.setViewportView(selectionPanels.get(type));
            }
        } else if (selectedSlots[0] != null && selectedType[0] != -1){
            selectedSlots[0].setText("");
            selectedSlots[0].setIcon(clickedSlot.getIcon());
        } else {
            selectionWrap.setViewportView(selectionPanels.getLast());
            //throw new ArrayIndexOutOfBoundsException("Error, could not find right selection panel! \n Detailed log: \n selectedSlots[0] = %d, selectedType[0] = %d \n selectedSlots[1] = %d, selectedType[1] %d");
        }
    }

    private void handleSlotEntered(JLabel enteredSlot, int type, int isSelection) {

        //Font curFont = enteredSlot.getFont();
        //System.out.printf("Entered - Font: %s; Size: %d; toString: %s\n", curFont.getFontName(), curFont.getSize(), curFont.toString());

        if(enteredSlot != selectedSlots[isSelection]) {
            enteredSlot.setBorder(HOVERED_BORDER);
        }
        enteredSlot.setBackground(COLOR_DARK.brighter());

    }

    private void handleSlotExited(JLabel exitedSlot, int type, int isSelection) {

        if(exitedSlot != selectedSlots[isSelection]) {
            exitedSlot.setBorder(UNSELECTED_BORDER);
        }
        exitedSlot.setBackground(COLOR_DARK);
    }

    private int fileSearch(int type) {
        String directoryPath;
        StringBuilder sb = new StringBuilder(100); // make a static variable
        sb.setLength(0);
        sb.append(DBD_ABS_PATH);
        sb.append(DBD_REL_PATHS[type]);
        directoryPath = sb.toString();

        // Create a File object for the specified directory
        File directory = new File(directoryPath);
        int imageNum = 0;
        // 2. Check if the directory exists and is actually a directory
        if (directory.exists() && directory.isDirectory()) {
            // 3. Get the list of files and subdirectories
            File[] files = directory.listFiles();

            if (files != null) {
                List<String> fileNamesList = new ArrayList<>();

                // 5. Iterate through the File array and add names to the list
                for (File file : files) {
                    fileNamesList.add(file.getName());
                    if(file.getName().contains(".png") && !file.getName().contains("empty") && !file.getName().contains("Missing")) {
                        try {
                            Image fileToImage = ImageIO.read(file);
                            if(type == 5 || type == 6) {
                                fileToImage = fileToImage.getScaledInstance(SLOT_WIDTH*2, SLOT_HEIGHT*2, Image.SCALE_SMOOTH);
                            } else {
                                fileToImage = fileToImage.getScaledInstance(SLOT_WIDTH, SLOT_HEIGHT, Image.SCALE_SMOOTH);
                            }
                            ImageIcon imageToIcon = new ImageIcon(fileToImage);
                            selectionItems.get(type).add(new slotItem(file.getName(), imageToIcon, type));
                            imageNum++;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } else if (file.exists() && file.isDirectory()){
                        File[] filesSub = file.listFiles();
                        //System.out.printf("%s\n", file.getAbsolutePath());
                        if(filesSub != null) {

                            for (File subfile : filesSub) {
                                fileNamesList.add(subfile.getName());
                                //System.out.printf("%s/%s\n", file.getName(), subfile.getName());
                                if (subfile.getName().contains(".png") && !subfile.getName().contains("empty") && !subfile.getName().contains("Missing")) {
                                    try {
                                        //System.out.printf("%s -> %s -> %s\n", directory.getName(), file.getName(), subfile.getName());
                                        Image fileToImage = ImageIO.read(subfile);
                                        fileToImage = fileToImage.getScaledInstance((int) (SLOT_WIDTH), (int) (SLOT_HEIGHT), Image.SCALE_SMOOTH);
                                        ImageIcon imageToIcon = new ImageIcon(fileToImage);
                                        selectionItems.get(type).add(new slotItem(subfile.getName(), imageToIcon, type));
                                        imageNum++;
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }

                // 6. Convert the List to a String array (if required)
                String[] fileNamesArray = fileNamesList.toArray(new String[0]);

                // 7. Print the file names (for demonstration)
                System.out.println("Files in directory: " + directoryPath);
                for (String fileName : fileNamesArray) {
                    System.out.println(fileName);
                }
            } else {
                System.out.println("Could not list files in the directory. It might be empty or an I/O error occurred.");
            }
        } else {
            System.out.println("The specified path does not exist or is not a directory.");
        }
        return imageNum;
    }

    // ------------------------------------------------------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}