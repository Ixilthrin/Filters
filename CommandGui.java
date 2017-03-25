package filters;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

public class CommandGui extends JFrame {
    private static final int VERSION = 3;
    FilterChainPanel chainPanel;

    JPanel stdoutPanel;
    JPanel stderrPanel;

    CardLayout outputPanels;
    CardLayout errorPanels;

    JButton nextOutput;
    JButton previousOutput;
    JButton deleteButton;

    CommandPanel commandPanel;
    ClipboardPanel clipboardPanel;
    JPanel leftPanel;
    JPanel rightPanel;
    JSplitPane mainSplitPane;
    Container container;
    String directory;
    DataOutput dataOutput;
    DataInput dataInput;
    File historyFile;
    Vector outputPanelNames = new Vector();
    Vector errorPanelNames = new Vector();
    int counter = 0;
    int currentOutputIndex = 0;
    int currentErrorIndex = 0;
    public CommandGui() {
        super("Commando");

        outputPanels = new CardLayout();
        errorPanels = new CardLayout();

        directory = System.getProperty("user.dir");
        File userDir = new File(directory);
        historyFile = new File(userDir, ".commandohistory");
        FileInputStream input = null;
        try {
            input = new FileInputStream(historyFile);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find history file: " + historyFile);
        }
        if (historyFile.exists()) {
            dataInput = new DataInputStream(input);
        }
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e) {
        }
        container = getContentPane();
        createComponents();
        layoutComponents();
        addControlLogic();
	
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(historyFile);
                } catch (FileNotFoundException ex) {
                    System.out.println("Cannot find historyFile: " + historyFile);
                }
                dataOutput = new DataOutputStream(output);
                writeHistory(dataOutput);
                try {
                    output.flush();
                } catch (IOException ioe) {
                    // NYI
                }
            }
        });
    }
    public void createComponents() {
        nextOutput = new JButton("Next");
        previousOutput = new JButton("Previous");
        deleteButton = new JButton("Delete");

        leftPanel = new JPanel();
        rightPanel = new JPanel();
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        mainSplitPane.setContinuousLayout(true);
        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setResizeWeight(.5);

        stdoutPanel = new JPanel(outputPanels);
        stdoutPanel.setBorder(new TitledBorder(
                                     new EtchedBorder(EtchedBorder.LOWERED), "Standard Output"));
        stderrPanel = new JPanel(errorPanels);
        stderrPanel.setBorder(new TitledBorder(
                                      new EtchedBorder(EtchedBorder.LOWERED), "Standard Error"));

        chainPanel = new FilterChainPanel(this);
        clipboardPanel = new ClipboardPanel();
        clipboardPanel.setBorder(new TitledBorder(
                                       new EtchedBorder(EtchedBorder.LOWERED), "Clipboard"));
        
        if (dataInput == null) {
            //setSize(500, 500);
            setSize(800, 800);
            // Position
            Dimension size = getSize();
            Dimension screenSize = getToolkit().getScreenSize();
            if (size.width > screenSize.width || size.height > screenSize.height) {
                setLocation(0, 0);
            } else {
                setLocation((screenSize.width - size.width)/2, 
                            (screenSize.height - size.height)/2);
            }
        } else {
            try {
                int version = dataInput.readInt();
                switch(version) {
                case 3:
                    setLocation(dataInput.readInt(), dataInput.readInt());
                case 2:
                    Dimension size = new Dimension();
                    size.width = dataInput.readInt();
                    size.height = dataInput.readInt();
                    setSize(size);
                    break;
                case 1:
                    setSize(300, 200);
                }
                clipboardPanel.readHistory(dataInput);
            } catch (IOException e) {
                // NYI
            }
        }
        FilterEditPanel filterEditPanel = new FilterEditPanel(chainPanel);
        commandPanel = new CommandPanel(this, directory, filterEditPanel);
        setDefaultCommandHistory();
        if (dataInput != null) {
            commandPanel.readHistory(dataInput);
        }

        commandPanel.setBorder(new TitledBorder(
                                       new EtchedBorder(EtchedBorder.LOWERED), "Command Entry"));
        chainPanel.setSourceProvider(commandPanel.getSourceProvider());
        chainPanel.setDestinationProvider(
                commandPanel.getDestinationProvider());
        chainPanel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                String command = chainPanel.getSelectedValue();
                commandPanel.setCommandLine(command);
            }
        });
    }
    public void layoutComponents() {
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        rightPanel.setLayout(bag);

        c.insets.left = 2;
        c.insets.top = 2;
        c.gridx = 0; 
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;

        bag.setConstraints(previousOutput, c);
        rightPanel.add(previousOutput);

        c.gridx = 1; 
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;

        bag.setConstraints(nextOutput, c);
        rightPanel.add(nextOutput);

        c.gridx = 2; 
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;

        bag.setConstraints(deleteButton, c);
        rightPanel.add(deleteButton);

        c.gridx = 0; 
        c.gridy = 1;
        c.gridheight = 5;
        c.gridwidth = 4;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;

        bag.setConstraints(stdoutPanel, c);
        rightPanel.add(stdoutPanel);


        // Left Panel
        leftPanel.setLayout(bag);

        c.insets.left = 2;
        c.insets.top = 2;
        c.gridx = 0; 
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 4;
        c.weightx = 1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;

        bag.setConstraints(clipboardPanel, c);
        leftPanel.add(clipboardPanel);

        c.insets.left = 2;
        c.insets.top = 2;
        c.gridx = 0; 
        c.gridy = 2;
        c.gridheight = 1;
        c.gridwidth = 4;
        c.weightx = 1;
        c.weighty = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;

        bag.setConstraints(chainPanel, c);
        leftPanel.add(chainPanel);

        c.gridx = 0; 
        c.gridy = 4;
        c.gridheight = 1;
        c.gridwidth = 4;
        c.weightx = 1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;

        bag.setConstraints(commandPanel, c);
        leftPanel.add(commandPanel);
	
        c.gridx = 0; 
        c.gridy = 8;
        c.gridheight = 2;
        c.gridwidth = 4;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;

        bag.setConstraints(stderrPanel, c);
        leftPanel.add(stderrPanel);

        mainSplitPane.setLeftComponent(leftPanel);
        //mainSplitPane.setRightComponent(stdoutPanel);
        mainSplitPane.setRightComponent(rightPanel);

        bag = new GridBagLayout();
        container.setLayout(bag);
        c = new GridBagConstraints();
	
        c.gridx = 0; 
        c.gridy = 0;
        c.gridheight = 6;
        c.gridwidth = 9;
        c.weighty = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;

        bag.setConstraints(mainSplitPane, c);
        container.add(mainSplitPane);
	
        pack();
    }
    public void addControlLogic() {
        nextOutput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showNextOutput();
            }
        });
        previousOutput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPreviousOutput();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteCurrentOutput();
            }
        });
    }
    public static void main(String args[]) {
        CommandGui gui = new CommandGui();
        gui.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        gui.setVisible(true);
    }
    public void addToHistory(String commandLine) {
        commandPanel.addToHistory(commandLine);
    }
    public boolean getAppendText() {
        return commandPanel.getAppendText();
    }
    public void writeHistory(DataOutput output) {
        try {
            output.writeInt(VERSION);
            Point p = getLocation();
            output.writeInt(p.x);
            output.writeInt(p.y);
            Dimension size = getSize();
            output.writeInt(size.width);
            output.writeInt(size.height);
            clipboardPanel.writeHistory(output);
            commandPanel.writeHistory(output);
        } catch (IOException e) {
            // NYI
        }
    }
    public void addOutputPanel(TextPanel t) {
        String uniqueName = new Integer(counter).toString();
        ++counter;
        stdoutPanel.add(t, uniqueName);
        outputPanels.show(stdoutPanel, uniqueName);
        outputPanelNames.add(uniqueName);
        currentOutputIndex = outputPanelNames.size() - 1;
    }
    public void addErrorPanel(TextPanel t) {
        String uniqueName = new Integer(counter).toString();
        ++counter;
        stderrPanel.add(t, uniqueName);
        errorPanels.show(stderrPanel, uniqueName);
        errorPanelNames.add(uniqueName);
        currentErrorIndex = errorPanelNames.size() - 1;
    }
    public void showPreviousOutput() {
        if (currentOutputIndex == 0) {
            return;
        }

        --currentOutputIndex;
        String name = (String) outputPanelNames.elementAt(currentOutputIndex);
        outputPanels.show(stdoutPanel, name);

        --currentErrorIndex;
        name = (String) errorPanelNames.elementAt(currentErrorIndex);
        errorPanels.show(stderrPanel, name);
    }
    public void showNextOutput() {
        if (currentOutputIndex == outputPanelNames.size() - 1) {
            return;
        }

        ++currentOutputIndex;
        String name = (String) outputPanelNames.elementAt(currentOutputIndex);
        outputPanels.show(stdoutPanel, name);

        ++currentErrorIndex;
        name = (String) errorPanelNames.elementAt(currentErrorIndex);
        errorPanels.show(stderrPanel, name);
    }
    public void deleteCurrentOutput() {
    }
    public void setDefaultCommandHistory() {
        // Insert default tools into command history for convenience.
        File file = new File(".");
        File files[] = file.listFiles();
        for (int i = 0; i < files.length; ++i) {
            String name = files[i].getName();
            if (name.endsWith(".java")) {
                name = name.substring(0, name.length() - 5);
                try {
                    Class c = Class.forName("filters." + name);
                    Class s = c.getSuperclass();
                    if (s != null) {
                        String parent = s.getName();
                        if (parent != null && 
                            parent.equals("filters.Filter")) {
                            commandPanel.addToHistory(name);
                        }
                    }
                } catch (ClassNotFoundException e) {
                }
            }
        }
    }
}
