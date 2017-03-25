package filters;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

public class FilterChainPanel extends JPanel {
    private final static int VERSION = 1;
    CardLayout cards;
    Vector filterLists;  // Vector<JList>
    JPanel filterListPanel;
    int currentFilterIndex;
    JButton previousButton;
    JButton nextButton;
    JButton executeButton;
    JButton deleteButton;

    String cwd; // current working directory
    DirectoryProvider sourceProvider;
    DirectoryProvider destinationProvider;
    Vector listSelectionListeners = new Vector();
    CommandGui gui;
    public FilterChainPanel(CommandGui g) {
        setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Filter Chains"));
        filterLists = new Vector();
        currentFilterIndex = 0;
        gui = g;
        createComponents();
        layoutComponents();
        addControlLogic();
    }
    public void createComponents() {
        JList filterList = createJList();
        filterLists.add(filterList);
        DefaultListModel model = new DefaultListModel();
        filterList.setModel(model);

        cards = new CardLayout();
        filterListPanel = new JPanel(cards);
        JScrollPane scroller = new JScrollPane(filterList);
        filterListPanel.add(scroller, 
                            new Integer(currentFilterIndex).toString());

        previousButton = new JButton("Previous");
        previousButton.setEnabled(false);
        nextButton = new JButton("Next");
        executeButton = new JButton("Execute Chain");
        deleteButton = new JButton("Delete Filter");
    }
    public void layoutComponents() {
        GridBagLayout bag = new GridBagLayout();
        setLayout(bag);
        GridBagConstraints c = new GridBagConstraints();

        cards.show(filterListPanel, 
                   new Integer(currentFilterIndex).toString());

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;

        bag.setConstraints(previousButton, c);
        add(previousButton);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;

        bag.setConstraints(nextButton, c);
        add(nextButton);

        JPanel separator = new JPanel();

        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 3;
        c.weightx = 1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;

        bag.setConstraints(separator, c);
        add(separator);

        c.gridx = 0; 
        c.gridy = 1;
        c.gridheight = 15;
        c.gridwidth = 5;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;

        bag.setConstraints(filterListPanel, c);
        add(filterListPanel);

        c.gridx = 0;
        c.gridy = 16;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;

        bag.setConstraints(executeButton, c);
        add(executeButton);

        c.gridx = 2;
        c.gridy = 16;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;

        bag.setConstraints(deleteButton, c);
        add(deleteButton);
    }
    public void addControlLogic() {
        previousButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentFilterIndex > 0) {
                    --currentFilterIndex;
                    cards.show(filterListPanel, 
                               new Integer(currentFilterIndex).toString());
                }
                if (currentFilterIndex == 0) {
                    previousButton.setEnabled(false);
                }
            }
        });
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // If the current filter chain is the last one, 
                // add a new one.
                if (currentFilterIndex + 1 == filterLists.size()) {
                    JList list = createJList();
                    filterLists.add(list);
                    DefaultListModel model = new DefaultListModel();
                    list.setModel(model);
                    ++currentFilterIndex;
                    JScrollPane scroller = new JScrollPane(list);
                    filterListPanel.add(scroller, 
                            new Integer(currentFilterIndex).toString());
                    cards.show(filterListPanel, 
                               new Integer(currentFilterIndex).toString());
                } else {
                    ++currentFilterIndex;
                    cards.show(filterListPanel, 
                               new Integer(currentFilterIndex).toString());
                }
                previousButton.setEnabled(true);
            }
        });
        executeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JList list = 
                    (JList) filterLists.elementAt(currentFilterIndex);
                DefaultListModel model = (DefaultListModel) list.getModel();
                FilterChain chain = FilterChain.createFilterChain(model,
                                                                  sourceProvider,
                        destinationProvider);
                chain.execute("unknown", "unkown", gui);
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteFilter();
            }
        });
    }
    public void addFilter(String filter) {
        gui.addToHistory(filter);
        if (filter != null && !filter.trim().equals("")) {
            StringTokenizer tokenizer = 
                new StringTokenizer(filter, "|");
            while (tokenizer.hasMoreTokens()) {
                JList list = 
                    (JList) filterLists.elementAt(currentFilterIndex);
                DefaultListModel model = 
                    (DefaultListModel) list.getModel();
                model.addElement(tokenizer.nextToken().trim());
            }
        }
    }
    public void insertAbove(String filter) {
        gui.addToHistory(filter);
        if (filter != null && !filter.trim().equals("")) {
            JList list = (JList) filterLists.elementAt(currentFilterIndex);
            int i = list.getSelectedIndex();
            if (i < 0) {
                i = 0;
            }
            StringTokenizer tokenizer = 
                new StringTokenizer(filter, "|");
            while (tokenizer.hasMoreTokens()) {
                DefaultListModel model = 
                    (DefaultListModel) list.getModel();
                model.add(i++, tokenizer.nextToken().trim());
            }
        }
    }
    public void insertBelow(String filter) {
        gui.addToHistory(filter);
        if (filter != null && !filter.trim().equals("")) {
            JList list = 
                (JList) filterLists.elementAt(currentFilterIndex);
            int i = list.getSelectedIndex();
            DefaultListModel model = 
                (DefaultListModel) list.getModel();
            int size = model.getSize();
            StringTokenizer tokenizer = 
                new StringTokenizer(filter, "|");
            while (tokenizer.hasMoreTokens()) {
                if (i < 0 || size - 1 == i) {
                    model.addElement(tokenizer.nextToken().trim());
                } else {
                    model.add(++i, tokenizer.nextToken().trim());
                }
            }
        }
    }
    public void replaceFilter(String newFilter) {
        gui.addToHistory(newFilter);
        JList list = (JList) filterLists.elementAt(currentFilterIndex);
        int i = list.getSelectedIndex();
        if (i >= 0) {
            DefaultListModel model = 
                (DefaultListModel) list.getModel();

            if (newFilter == null || newFilter.trim().equals("")) {
                model.removeElementAt(i);
            } else {
                model.setElementAt(newFilter, i);
            }
        }
    }
    public void deleteFilter() {
        JList list = (JList) filterLists.elementAt(currentFilterIndex);
        int i = list.getSelectedIndex();
        if (i >= 0) {
            DefaultListModel model = 
                (DefaultListModel) list.getModel();
            model.removeElementAt(i);
        }
    }
    public void addListSelectionListener(ListSelectionListener listener) {
        listSelectionListeners.add(listener);
        // Add the listener to each JList.
        Iterator iter = filterLists.iterator();
        while (iter.hasNext()) {
            JList l = (JList) iter.next();
            l.addListSelectionListener(listener);
        }
    }
    public JList createJList() {
        JList list = new JList();
        // Add each listener to the new JList.
        Iterator iter = listSelectionListeners.iterator();
        while (iter.hasNext()) {
            ListSelectionListener l = (ListSelectionListener) iter.next();
            list.addListSelectionListener(l);
        }
        return list;
    }
    public String getSelectedValue() {
        JList list = (JList) filterLists.elementAt(currentFilterIndex);
        Object value = list.getSelectedValue();
        if (value == null) {
            return "";
        } 
        return value.toString();
    }
    public void setSourceProvider(DirectoryProvider d) {
        sourceProvider = d;
    }
    public void setDestinationProvider(DirectoryProvider d) {
        destinationProvider = d;
    }
    public void readHistory(DataInput input) {
        try {
            int unused = input.readInt(); //  VERSION == 1
            int chainCount = input.readInt();
            for (int i = 0; i < chainCount; ++i) {
                int filterCount = input.readInt();
                JList filterList = createJList();
                boolean wasNotEmpty = false;
                for (int j = 0 ; j < filterCount; ++j) {
                    String nextFilter = input.readUTF();
                    Object genericModel = filterList.getModel();
                    if (genericModel instanceof DefaultListModel) {
                        DefaultListModel model = 
                            (DefaultListModel) genericModel;
                        model.addElement(nextFilter);
                        wasNotEmpty = false;
                    }
                }
                if (wasNotEmpty) {
                    filterLists.add(filterList);
                }
            }
        } catch (IOException e) {
            // NYI
        }
    }
    public void writeHistory(DataOutput output) {
        try {
            output.writeInt(VERSION);
            int chainCount = filterLists.size();
            output.writeInt(chainCount);
            for (int i = 0; i < chainCount; ++i) {
                JList filterList = (JList) filterLists.elementAt(i);
                Object genericModel = filterList.getModel();
                if (genericModel instanceof DefaultListModel) {
                    DefaultListModel model = (DefaultListModel) genericModel;
                    int filterCount = model.getSize();
                    output.writeInt(filterCount);
                    for (int j = 0; j < filterCount; ++j) {
                        String s = (String) model.getElementAt(j);
                        output.writeUTF(s);
                    }
                } else {
                    output.writeInt(0);
                }
            }
            
        } catch (IOException e) {
            // NYI
        }
    }
}
