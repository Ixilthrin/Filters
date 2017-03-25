package filters;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

class DirectoryPanel extends JPanel implements DirectoryProvider {
    private final static int VERSION = 1;
    JComboBox directoryCombo;
    JButton browseButton;
    JButton deleteButton;
    String initialDirectory;
    boolean enabled = true;
    public DirectoryPanel(String d) {
        initialDirectory = d;
        createComponents();
        layoutComponents();
        addControlLogic();
    }
    public void createComponents() {
        directoryCombo = new JComboBox();
        directoryCombo.setEnabled(enabled);
        directoryCombo.addItem(initialDirectory);
        directoryCombo.setSelectedItem(initialDirectory);
        directoryCombo.setEditable(true);
        browseButton = new JButton("Browse...");
        deleteButton = new JButton("Delete");
        browseButton.setEnabled(enabled);
    }
    public void layoutComponents() {
        GridBagLayout bag = new GridBagLayout();
        setLayout(bag);
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0; 
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 5;
        c.weightx = 1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;

        bag.setConstraints(directoryCombo, c);
        add(directoryCombo);

        c.gridx = 5; 
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;

        bag.setConstraints(browseButton, c);
        add(browseButton);

        c.gridx = 6; 
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 3;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;

        bag.setConstraints(deleteButton, c);
        add(deleteButton);
    }
    public void addControlLogic() {
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(
                        directoryCombo.getSelectedItem().toString());
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(DirectoryPanel.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String selected = chooser.getSelectedFile().toString();
                    directoryCombo.insertItemAt(selected, 0);
                    directoryCombo.setSelectedItem(selected);
                }
            }
        });
        Action delete = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                delete();
            }
        };
        deleteButton.addActionListener(delete);

        directoryCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Move selected item to top of list.
                Object selected = directoryCombo.getSelectedItem();
                directoryCombo.removeItem(selected);
                directoryCombo.insertItemAt(selected, 0);
                directoryCombo.setSelectedIndex(0);
            }
        });
    }
    public void delete() {
        //directoryCombo.removeItemAt(0);
    }
    public void setEnabled(boolean e) {
        enabled = e;
        directoryCombo.setEnabled(e);
        browseButton.setEnabled(e);
    }
    public boolean isEnabled() {
        return enabled;
    }
    public String getDirectory() {
        if (!enabled) {
            return "";
        }
        Object selection = directoryCombo.getSelectedItem();
        if (selection != null) {
            String strSelection = selection.toString();
            int itemCount = directoryCombo.getItemCount();
            boolean containsIt = false;
            for (int i = 0; i < itemCount; ++i) {
                if (directoryCombo.getItemAt(i).toString().trim().equals(strSelection)) {
                    containsIt = true;
                }
            }
            if (!containsIt) {
                directoryCombo.insertItemAt(strSelection, 0);
            }
            return strSelection;
        }
        return null;
    }
    public void readHistory(DataInput input) {
        try {
            int unused = input.readInt(); // VERSION = 1 
            int commandCount = input.readInt();
            if (directoryCombo == null) {
                directoryCombo = new JComboBox();
            }
            for (int i = 0; i < commandCount; ++i) {
                String nextString = input.readUTF();
                int itemCount = directoryCombo.getItemCount();
                boolean containsIt = false;
                for (int j = 0; j < itemCount; ++j) {
                    if (directoryCombo.getItemAt(j).toString().trim().equals(nextString)) {
                        containsIt = true;
                    }
                }
                if (!containsIt) {
                    directoryCombo.addItem(nextString);
                }
            }
        } catch (IOException e) {
            // NYI
        }
    }
    public void addEditorKeyListener(KeyListener listener) {
        Component editor = directoryCombo.getEditor().getEditorComponent();
        editor.addKeyListener(listener);
    }
    public void addComboBoxActionListener(ActionListener listener) {
        directoryCombo.addActionListener(listener);
    }
    public void writeHistory(DataOutput output) {
        try {
            output.writeInt(VERSION);
            int comboCount = directoryCombo.getItemCount();
            Vector strings = new Vector();
            for (int i = 0; i < comboCount; ++i) {
                String next = (String) directoryCombo.getItemAt(i);
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
}
