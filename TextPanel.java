package filters;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

class TextPanel extends JPanel {
    JLabel command;
    JLabel path;
    AutoScrollTextArea textArea;
    JButton clearButton;
    JToggleButton pauseButton;
    JCheckBox autoScroll;
    int rows;
    int columns;
    public TextPanel(int r, int c) {
        rows = r;
        columns = c;
        createComponents();
        layoutComponents();
        addControlLogic();
    }
    public void setCommand(String c) {
        command.setText(c);
        command.repaint();
    }
    public void setPath(String p) {
        path.setText(p);
        path.repaint();
    }
    public void createComponents() {
        command = new JLabel();
        path = new JLabel();
        textArea = new AutoScrollTextArea(rows, columns);
        clearButton = new JButton("Clear");
        pauseButton = new JToggleButton("Pause");
        autoScroll = new JCheckBox("Autoscroll");
    }
    public void layoutComponents() {
        GridBagLayout bag = new GridBagLayout();
        setLayout(bag);
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0; 
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;

        bag.setConstraints(command, c);
        add(command);

        c.gridx = 0; 
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;

        bag.setConstraints(path, c);
        add(path);

        c.gridx = 0; 
        c.gridy = 2;
        c.gridheight = 4;
        c.gridwidth = 4;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;

        JScrollPane scroller = new JScrollPane(textArea);
        bag.setConstraints(scroller, c);
        add(scroller);
	
        c.gridx = 0;
        c.gridy = 6;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;

        bag.setConstraints(autoScroll, c);
        add(autoScroll);
	
        c.gridx = 2;
        c.gridy = 6;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;

        bag.setConstraints(pauseButton, c);
        add(pauseButton);
	
        c.gridx = 3;
        c.gridy = 6;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;

        bag.setConstraints(clearButton, c);
        add(clearButton);
    }
    public void addControlLogic() {
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setIsPaused(!textArea.getIsPaused());
            }
        });
        autoScroll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setAutoScroll(autoScroll.isSelected());
            }
        });
    }
    public JTextArea getTextArea() {
        return textArea;
    }
}
