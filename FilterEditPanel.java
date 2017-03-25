package filters;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class FilterEditPanel extends JPanel {
    FilterChainPanel chainPanel;
    CommandPanel commandPanel;
    JButton addButton;
    JButton insertAboveButton;
    JButton insertBelowButton;
    JButton replaceButton;
    JButton deleteButton;
    public FilterEditPanel(FilterChainPanel panel) {
	chainPanel = panel;
	createComponents();
	layoutComponents();
	addControlLogic();
    }
    public void createComponents() {
	addButton = new JButton("Add");
	insertAboveButton = new JButton("Insert Above");
	insertBelowButton = new JButton("Insert Below");
	replaceButton = new JButton("Replace");
	deleteButton = new JButton("Delete");
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

        bag.setConstraints(addButton, c);
	add(addButton);

	c.gridx = 1;
	c.gridy = 0;
	c.gridheight = 1;
	c.gridwidth = 1;
	c.weightx = 0;
	c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;

        bag.setConstraints(insertAboveButton, c);
	add(insertAboveButton);

	c.gridx = 2;
	c.gridy = 0;
	c.gridheight = 1;
	c.gridwidth = 1;
	c.weightx = 0;
	c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;

        bag.setConstraints(insertBelowButton, c);
	add(insertBelowButton);

	c.gridx = 3;
	c.gridy = 0;
	c.gridheight = 1;
	c.gridwidth = 1;
	c.weightx = 0;
	c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;

        bag.setConstraints(replaceButton, c);
	add(replaceButton);

	JPanel separator = new JPanel();

	c.gridx = 4;
	c.gridy = 0;
	c.gridheight = 1;
	c.gridwidth = 2;
	c.weightx = 1;
	c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;

        bag.setConstraints(separator, c);
	add(separator);

	/*
	c.gridx = 4;
	c.gridy = 0;
	c.gridheight = 1;
	c.gridwidth = 1;
	c.weightx = 0;
	c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;

        bag.setConstraints(deleteButton, c);
	add(deleteButton);
	*/
    }
    public void addControlLogic() {
	addButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (commandPanel != null) {
		    String filter = commandPanel.getCommandLine();
		    chainPanel.addFilter(filter);
		}
	    }
	});
	insertAboveButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (commandPanel != null) {
		    String filter = commandPanel.getCommandLine();
		    chainPanel.insertAbove(filter);
		}
	    }
	});
	insertBelowButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (commandPanel != null) {
		    String filter = commandPanel.getCommandLine();
		    chainPanel.insertBelow(filter);
		}
	    }
	});
	replaceButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (commandPanel != null) {
		    String filter = commandPanel.getCommandLine();
		    chainPanel.replaceFilter(filter);
		}
	    }
	});
	deleteButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		chainPanel.deleteFilter();
	    }
	});
    }
    public void setCommandPanel(CommandPanel p) {
	commandPanel = p;
    }
}
