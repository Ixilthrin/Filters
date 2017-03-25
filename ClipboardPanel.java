package filters;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

class ClipboardPanel extends JPanel implements ClipboardOwner {
    private final static int VERSION = 1;
    JButton getButton;
    JButton putButton;
    JButton deleteButton;
    JCheckBox autoPutCheckBox;
    JComboBox stringCombo;
    public ClipboardPanel() {
        createComponents();
        layoutComponents();
        addControlLogic();
    }
    public void createComponents() {
        getButton = new JButton("Get");
        putButton = new JButton("Put");
        deleteButton = new JButton("Delete");
        autoPutCheckBox = new JCheckBox("Autoput");
        autoPutCheckBox.setSelected(false);
        if (stringCombo == null) {
            stringCombo = new JComboBox();
        }
        stringCombo.setEditable(false);
    }
    public void layoutComponents() {
        GridBagLayout bag = new GridBagLayout();
        setLayout(bag);
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0; 
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 3;
        c.weightx = 1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;

        bag.setConstraints(stringCombo, c);
        add(stringCombo);
	
        c.gridx = 3;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;

        bag.setConstraints(getButton, c);
        add(getButton);
	
        c.gridx = 4;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;

        bag.setConstraints(putButton, c);
        add(putButton);
	
        c.gridx = 5;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;

        bag.setConstraints(deleteButton, c);
        add(deleteButton);
	
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 4;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        bag.setConstraints(autoPutCheckBox, c);
        add(autoPutCheckBox);
    }
    public void addControlLogic() {
        Action get = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                get();
            }
        };
        getButton.addActionListener(get);

        Action put = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                put();
            }
        };
        putButton.addActionListener(put);

        Action delete = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                delete();
            }
        };
        deleteButton.addActionListener(delete);

        stringCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (autoPutCheckBox.isSelected()) {
                    put();
                }
            }
        });
    }
    // Copies from clipboard into the combo box
    public void get() {
        String s = getClipboardData();
        s = s.trim();
        if (s.equals("")) {
            return;
        }
        int comboCount = stringCombo.getItemCount();
        boolean containsIt = false;
        for (int i = 0; i < comboCount; ++i) {
            if (stringCombo.getItemAt(i).toString().trim().equals(s)) {
                containsIt = true;
            }
        }
        if (!containsIt) {
            stringCombo.addItem(s);
            stringCombo.setSelectedItem(s);
        }
    }
    public void put() {
        Object selectedItem = stringCombo.getSelectedItem();
        if (selectedItem instanceof String) {
            setClipboardData((String) selectedItem);
        }
    }
    public void delete() {
        Object selectedItem = stringCombo.getSelectedItem();
        stringCombo.removeItem(selectedItem);
    }
    /**
     *  Retrieves text from the system clipboard.
     */
    public String getClipboardData() {
        Transferable clip = null;
        try {
            Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
            clip = cp.getContents(this);            
        }
        catch (Exception e) {
        }
        if (clip == null) {
            return "";
        }
        String text = null;
        try {
            text = (String) clip.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception e) {
        }
        if (text == null) {
            return "";
        }
        return text;
    }
    /**
     *  Puts text on the system clipboard.
     */
    public void setClipboardData(final String text) {
        Transferable clip = new Transferable() {
            public Object getTransferData(DataFlavor data) {
                return text;
            }
            public DataFlavor[] getTransferDataFlavors() {
                DataFlavor flavors[] = { DataFlavor.stringFlavor };
                return flavors;
            }
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return true;
            }
        };
        try {
            Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
            cp.setContents(clip, this);
        } catch (Exception e) {
        }
    }
    public void readHistory(DataInput input) {
        try {
            int unused = input.readInt(); // VERSION = 1 
            int stringCount = input.readInt();
            if (stringCombo == null) {
                stringCombo = new JComboBox();
            }
            for (int i = 0; i < stringCount; ++i) {
                String next = input.readUTF();
                stringCombo.addItem(next);
            }
        } catch (IOException e) {
            // NYI
        }
    }
    public void writeHistory(DataOutput output) {
        try {
            output.writeInt(VERSION);
            int stringCount = stringCombo.getItemCount();
            Vector strings = new Vector();
            for (int i = 0; i < stringCount; ++i) {
                String next= (String) stringCombo.getItemAt(i);
                if (!next.trim().equals("")) {
                    strings.add(next);
                }
            }
            output.writeInt(strings.size());
            Iterator iter = strings.iterator();
            while (iter.hasNext()) {
                output.writeUTF((String) iter.next());
            }
        } catch (IOException e) {
            // NYI
        }
    }
    /**
     *  Part of the ClipboardOwner interface.
     *  This method does nothing.
     */
    public void lostOwnership(Clipboard b, Transferable t) {
    }
}
