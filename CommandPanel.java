package filters;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

class CommandPanel extends JPanel {
    private final static int VERSION = 1;
    JButton runButton;
    JButton clearButton;
    JButton deleteButton;
    boolean isRunning = false;
    /**
     *  The kill button is not currently supported since java only
     *  supports killing the parent process, but not the children.
     *  This is a problem when, for instance, sh is executed
     *  on a script.  When sh is killed, the processes invoked by sh
     *  will still be active, which makes the kill command look like
     *  it did not work
     */
    JButton killButton;
    JCheckBox autoRunCheckBox;
    JCheckBox autoClearCheckBox;
    JCheckBox appendCheckBox;
    JComboBox commandCombo;
    boolean autoRunEnabled = true;

    FilterEditPanel filterEditPanel;
    DirectoryPanel sourcePanel;
    DirectoryPanel destinationPanel;
    JCheckBox enableDestinationCheckBox;
    String initialDirectory;
    CommandGui gui;
    public CommandPanel(CommandGui g, String dir, FilterEditPanel filterEdit) {
        gui = g;
        initialDirectory = dir;

        filterEditPanel = filterEdit;
        filterEditPanel.setCommandPanel(this);

        createComponents();
        layoutComponents();
        addControlLogic();
    }
    public void createComponents() {
        runButton = new JButton("Run");
        clearButton = new JButton("Clear");
        deleteButton = new JButton("Delete");
        autoRunCheckBox = new JCheckBox("Autorun");
        autoClearCheckBox = new JCheckBox("Autoclear");
        autoRunCheckBox.setSelected(false);
        autoClearCheckBox.setSelected(true);
        appendCheckBox = new JCheckBox("Append output");
        if (commandCombo == null) {
            commandCombo = new JComboBox();
        }
        commandCombo.setEditable(true);
        sourcePanel = new DirectoryPanel(initialDirectory);
        destinationPanel = new DirectoryPanel(initialDirectory);
        destinationPanel.setEnabled(false);
        enableDestinationCheckBox = new JCheckBox("Target Dir");
    }
    public void layoutComponents() {
        GridBagLayout bag = new GridBagLayout();
        setLayout(bag);
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0; 
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 6;
        c.weightx = 1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;

        bag.setConstraints(sourcePanel, c);
        add(sourcePanel);

        c.gridx = 0; 
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 3;
        c.weightx = 1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;

        bag.setConstraints(commandCombo, c);
        add(commandCombo);
	
        c.gridx = 3;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;

        bag.setConstraints(runButton, c);
        add(runButton);
	
        c.gridx = 4;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;

        bag.setConstraints(clearButton, c);
        add(clearButton);
	
        c.gridx = 5;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;

        bag.setConstraints(deleteButton, c);
        add(deleteButton);
	
        c.gridx = 0;
        c.gridy = 2;
        c.gridheight = 1;
        c.gridwidth = 4;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        bag.setConstraints(filterEditPanel, c);
        add(filterEditPanel);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;

        bag.setConstraints(autoRunCheckBox, c);
        add(autoRunCheckBox);

        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;

        bag.setConstraints(autoClearCheckBox, c);
        add(autoClearCheckBox);
	
        c.gridx = 2;
        c.gridy = 3;
        c.gridwidth = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;

        bag.setConstraints(appendCheckBox, c);
        add(appendCheckBox);

        c.gridx = 0; 
        c.gridy = 4;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;

        bag.setConstraints(enableDestinationCheckBox, c);
        add(enableDestinationCheckBox);

        c.gridx = 1; 
        c.gridy = 4;
        c.gridheight = 1;
        c.gridwidth = 5;
        c.weightx = 1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;

        bag.setConstraints(destinationPanel, c);
        add(destinationPanel);
    }
    public void addControlLogic() {
        Action run = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                run();
            }
        };
        runButton.addActionListener(run);

        Action clear = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        };
        clearButton.addActionListener(clear);

        Action delete = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                delete();
            }
        };
        deleteButton.addActionListener(delete);

        commandCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (autoRunCheckBox.isSelected() && autoRunEnabled) {
                    run();
                }
            }
        });
        enableDestinationCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                destinationPanel.setEnabled(
                        enableDestinationCheckBox.isSelected());
            }
        });

        KeyAdapter adapter = new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_ENTER) {
                    if (!autoRunCheckBox.isSelected()) {
                        run();
                    }
                } else if (code == KeyEvent.VK_UP) {
                    autoRunEnabled = false;
                } else if (code == KeyEvent.VK_DOWN) {
                    autoRunEnabled = false;
                }
            }};
        
        Component editor = commandCombo.getEditor().getEditorComponent();
        editor.addKeyListener(adapter);
        sourcePanel.addEditorKeyListener(adapter);
        sourcePanel.addComboBoxActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (autoRunCheckBox.isSelected() && autoRunEnabled) {
                    run();
                }
            }
        });
    }

    public void run() {
        if (isRunning) {
            return;
        }
        autoRunEnabled = true;
        Object selectedItem = commandCombo.getSelectedItem();
        String commandLine = null;
        if (selectedItem == null) {
            return;
        } else {
            commandLine = selectedItem.toString();
        }

        isRunning = true;
        runButton.setEnabled(false);

        final String line = commandLine;
        final String path = sourcePanel.getDirectory();
	
        // Parse the command separator ';'
        Thread runner = new Thread() {
            public void run() {
                StringTokenizer tokenizer = new StringTokenizer(line, ";");
                while (tokenizer.hasMoreTokens()) {
                    FilterChain chain = 
                        FilterChain.createFilterChain(
                                tokenizer.nextToken(),
                                sourcePanel,
                                destinationPanel);
                    chain.execute(line, path, gui);
                    while (isRunning) {
                        isRunning = chain.isRunning();
                        if (isRunning) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                // Just continue silently
                            }
                        }
                    }
                }
                isRunning = false;
                runButton.setEnabled(true);
            }};
        runner.start();
        addToHistory(commandLine);
        if (autoClearCheckBox.isSelected()) {
            clear();
        }
    }
    public void clear() {
        commandCombo.getEditor().setItem("");
    }
    public void delete() {
        Object selectedItem = commandCombo.getSelectedItem();
        commandCombo.removeItem(selectedItem);
    }
    public void addToHistory(String commandLine) {
        // Add the item to the top of the list.
        commandCombo.removeItem(commandLine);
        commandCombo.insertItemAt(commandLine, 0);
        commandCombo.setSelectedIndex(0);
    }
    public String getCommandLine() {
        Object selectedItem = commandCombo.getSelectedItem();
        if (selectedItem == null) {
            return "";
        }
        return selectedItem.toString();
    }
    public void setCommandLine(String line) {
        addToHistory(line);
        commandCombo.setSelectedItem(line);
    }
    public DirectoryProvider getSourceProvider() {
        return sourcePanel;
    }
    public DirectoryProvider getDestinationProvider() {
        return destinationPanel;
    }
    public boolean getAppendText() {
        return appendCheckBox.isSelected();
    }
    public void readHistory(DataInput input) {
        try {
            int unused = input.readInt(); // VERSION = 1 
            sourcePanel.readHistory(input);
            destinationPanel.readHistory(input);
            int commandCount = input.readInt();
            if (commandCombo == null) {
                commandCombo = new JComboBox();
            }
            for (int i = 0; i < commandCount; ++i) {
                String nextCommand = input.readUTF();
                commandCombo.addItem(nextCommand);
            }
        } catch (IOException e) {
            // NYI
        }
        clear();
    }
    public void writeHistory(DataOutput output) {
        try {
            output.writeInt(VERSION);
            sourcePanel.writeHistory(output);
            destinationPanel.writeHistory(output);
            int comboCount = commandCombo.getItemCount();
            Vector commands = new Vector();
            for (int i = 0; i < comboCount; ++i) {
                String nextCommand = (String) commandCombo.getItemAt(i);
                if (!nextCommand.trim().equals("")) {
                    commands.add(nextCommand);
                }
            }
            output.writeInt(commands.size());
            Iterator iter = commands.iterator();
            while (iter.hasNext()) {
                output.writeUTF((String) iter.next());
            }
        } catch (IOException e) {
            // NYI
        }
    }
}
