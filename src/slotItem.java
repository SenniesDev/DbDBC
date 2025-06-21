import javax.swing.*;

public class slotItem {
    private String name;
    private ImageIcon imageIcon;
    private int type;
    //private JLabel label;

    public slotItem (String name, ImageIcon imageIcon, int type) {
        this.name = name;
        this.imageIcon = imageIcon;
        this.type = type;
        //this.label = label;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }


    // No setters present

    @Override
    public String toString() {
        return "item{" + "name='" + name + '\'' + ", type='" + type + '\'' + '}';
    }
}
