package filters;

import java.util.*;
//import java.awt.*;
//import java.awt.event.*;
import javax.swing.*;

public class FilterChain {
    protected String name;
    protected Vector filters;
    protected boolean executing = false;
    public FilterChain(String n) {
        name = n;
    }
    public FilterChain(String n, Vector f) {
        this(n);
        filters = f;
    }
    public String getName() {
        return name;
    }
    public Vector getFilters() {
        return filters;
    }
    public boolean equals(Object o) {
        if (!(o instanceof FilterChain)) {
            return false;
        }
        FilterChain otherChain = (FilterChain) o;
        int size = filters.size();
        if (size != otherChain.getFilters().size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (!(filters.elementAt(i).equals(
                          otherChain.getFilters().elementAt(i)))) {
                return false;
            }
        }
        return true;
    }
    public static FilterChain createFilterChain(DefaultListModel model,
                                                DirectoryProvider sourceProvider,
            DirectoryProvider destinationProvider) {
        // Create a Vector of Filters.
        Vector filters = new Vector();
        for (int i = 0; i < model.getSize(); ++i) {
            Object next = model.getElementAt(i);
            if (next != null) {
                String command = model.getElementAt(i).toString();
                Filter nextFilter = createFilter(command);
                if (nextFilter == null) {
                    nextFilter = new CommandFilter();
                    String args = command;
                    nextFilter.setArguments(args);
                }
                String cwd = sourceProvider.getDirectory();
                nextFilter.setDirectory(cwd);
                filters.add(nextFilter);
            }
        }
        return new FilterChain("", filters);
    }
    public static FilterChain createFilterChain(final String commandLine,
                                                DirectoryProvider sourceProvider,
            DirectoryProvider destinationProvider) {
        String line = commandLine + " " + 
            destinationProvider.getDirectory().toString();
        Vector filters = new Vector();
        StringTokenizer tokenizer = new StringTokenizer(line, "|");
        while (tokenizer.hasMoreTokens()) {
            String command = tokenizer.nextToken();
            Filter nextFilter = createFilter(command);
            if (nextFilter == null) {
                nextFilter = new CommandFilter();
                String args = command;
                nextFilter.setArguments(args);
            }
            String cwd = sourceProvider.getDirectory();
            nextFilter.setDirectory(cwd);
            filters.add(nextFilter);
        }
        FilterChain chain = new FilterChain("", filters);
        return chain;
    }
    /**
     *  If command represents a class in the filters
     *  package, then instantiate it and return it.
     *  If not, return null.
     */
    public static Filter createFilter(String command) {
        // NYI: A filter with arguments.
        command = command.trim();
        int argIndex = command.indexOf(" ");
        String name = null;
        if (argIndex > 1) {
            name = "filters." + command.substring(0, argIndex);
        } else {
            name = "filters." + command;
        }
        try {
            Class filterClass = Class.forName(name);
            if (filterClass == null) {
                return null;
            }
            // NYI: Make sure this cast is valid.
            Filter filter = (Filter) filterClass.newInstance();
            String args = null;
            if (argIndex > 1) {
                args = command.substring(argIndex, command.length());
            } else {
                args = "";
            }
            filter.setArguments(args);
            return filter;
        } catch (ClassNotFoundException e) {
            // Do nothing.
        } catch (InstantiationException e) {
            // Do nothing.
        } catch (IllegalAccessException e) {
            // Do nothing.
        } catch (NoClassDefFoundError e) {
            // Do nothing.
        }
        return null;
    }
    public void execute(String commandLine, String path, CommandGui gui) {
        executing = true;
        int size = filters.size();
        if (size < 1) {
            return;  // All done!
        }

        String title = "<Command> " + commandLine;
        String pathTitle = "<Path> " + path;

        TextPanel outputPanel = new TextPanel(10, 60);
        JTextArea outputTextArea = outputPanel.getTextArea();
        TextPanel errorPanel = new TextPanel(5, 60);
        JTextArea errorTextArea = errorPanel.getTextArea();
        gui.addOutputPanel(outputPanel);
        gui.addErrorPanel(errorPanel);

        outputPanel.setCommand(title);
        outputPanel.setPath(pathTitle);
        errorPanel.setCommand(title);
        errorPanel.setPath(pathTitle);

        // Connect the filters.
        for (int j = 0; j < size; ++j) {
            // NYI : Connect the first filter to useful input
            // like the input text area.
            Filter nextFilter = (Filter) filters.elementAt(j);
            if (j == 0) {
            }
            if (j == size - 1) {
                if (!nextFilter.outputIsConnected()) {
                    // The last filter connects to the text area or stdout.
                    // if not already connected.
                    if (outputTextArea == null) {
                        nextFilter.connectOutput(System.out);
                    } else {
                        nextFilter.connectOutput(outputTextArea);
                    }
                }

                // For command filters, connect stderr
                if (nextFilter instanceof CommandFilter) {
                    if (errorTextArea == null) {
                        ((CommandFilter) nextFilter).connectErrorOutput(
                                System.err);
                    } else {
                        ((CommandFilter) nextFilter).connectErrorOutput(
                                errorTextArea);
                    }
                }
            } else {
                nextFilter.connectOutput((Filter) filters.elementAt(j + 1));
            }
        }
	 
        // Finally, run it.
        ((Filter) filters.elementAt(0)).start();
        executing = false;
    }
    public boolean isRunning() {
        boolean filterIsAlive = false;
        Iterator iter = filters.iterator();
        while (iter.hasNext()) {
            Filter filter = (Filter) iter.next();
            if (filter.isAlive()) {
                filterIsAlive = true;
            }
        }
        return executing || filterIsAlive;
    }
}
