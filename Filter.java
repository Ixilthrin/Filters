package filters;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 *  Concrete Filter classes implement the doFilter() method.
 *  Filters are meant to be chained together.
 *  Chaining can be accomplished programmatically, or
 *  from the command-line using unix-style pipes.
 *  @see filters.Echo for example of an implementation of the
 *  main() method which enables command-line piping.
 */
public abstract class Filter extends Thread implements ClipboardOwner {
    protected Reader reader;
    protected Writer writer;
    /**
     *  outputFilter can be null since a filter does
     *  not need to output to another filter.
     *  It can output to a file, stdout, or a String.
     */
    protected Filter outputFilter;
    protected String args[];
    protected File cwd;  // current working directory
    public Filter () {
    }
    /** 
     *  Since filters are to be used from both the command
     *  line and programmatically, all arguments should be strings.
     *  To allow for simple reflection, make all contructors take
     *  no arguments and pass in the arguments with this function.
     *  @param argList is a list of arguments as found on a command line.
     */
    abstract public void setArguments(String argList);
    public void setDirectory(String directory) {
        if (directory != null) {
            File dir = new File(directory);
            if (dir.isDirectory()) {
                cwd = dir;
            }
        }
    }
    public boolean outputIsConnected() {
        return writer != null;
    }
    public boolean inputIsConnected() {
        return reader != null;
    }
    public void connectInputToClipboard() {
        connectInput(getClipboardData());
    }
    /**
     *  @param s The input string
     */
    public void connectInput(String s) {
        reader = new StringReader(s);
    }
    /**
     *  Use the toString() method to see the output.
     */
    public void connectOutput() {
        writer = new StringWriter();
        outputFilter = null;
    }
    public void connectInput(File input) {
        try {
            reader = new BufferedReader(new FileReader(input));
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
    public void connectOutput(File output) {
        outputFilter = null;
        try {
            writer = new BufferedWriter(new FileWriter(output));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    /**
     *  When using this, do not call connectOutput() on the other filter.
     *  The connection takes care of both sides.
     */
    public void connectInput(Filter filter) {
        try {
            filter.connectOutput(this);
            PipedInputStream input = new PipedInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            ((PipedInputStream) input).connect(filter.getNewPipedOutput());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    /**
     *  When using this, do not call connectInput() on the other filter.
     *  The connection takes care of both sides.
     */
    public void connectOutput(Filter filter) {
        try {
            outputFilter = filter;
            PipedOutputStream output = new PipedOutputStream();
            writer = new PrintWriter(output);
            ((PipedOutputStream) output).connect(filter.getNewPipedInput());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    public void connectInput(InputStream input) {
        reader = new BufferedReader(new InputStreamReader(input));
    }
    public void connectOutput(OutputStream output) {
        writer = new PrintWriter(output);
        outputFilter = null;
    }
    public void connectOutput(JTextArea area) {
        writer = new TextAreaWriter(area);
        outputFilter = null;
    }
    private PipedInputStream getNewPipedInput() {
        PipedInputStream input = new PipedInputStream();
        reader = new BufferedReader(new InputStreamReader(input));
        return (PipedInputStream) input;
    }
    private PipedOutputStream getNewPipedOutput() {
        PipedOutputStream output = new PipedOutputStream();
        writer = new PrintWriter(output);
        return (PipedOutputStream) output;
    }
    /**
     *  This is where the actual filtering is done.
     */
    abstract public String doFilter(String inputString);
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
    /**
     *  Part of the ClipboardOwner interface.
     *  This method does nothing.
     */
    public void lostOwnership(Clipboard b, Transferable t) {
    }
    /**
     *  Called by the framework after the thread's start() method
     *  is invoked.
     *  Each filter calls start() on its output.
     */
    public void run() {
        if (outputFilter != null) {
            outputFilter.start();
        }
        try {
            if (reader instanceof BufferedReader) {
                while (true) {
                    String inputLine = null;
                    inputLine = ((BufferedReader) reader).readLine();
                    if (inputLine == null) break;
                    String outputString = doFilter(inputLine);
                    if (writer != null && outputString != null) {
                        writer.write(outputString);
                        writer.flush();
                    }
                }
            } else if (reader == null) {
                if (writer != null) {
                    String outputString = doFilter("");
                    if (outputString != null) {
                        writer.write(outputString);
                        writer.flush();
                    }
                }
            } else {
                StringBuffer buffer = new StringBuffer();
                int next = 0;
                while (true) {
                    next = reader.read();
                    if (next != -1) {
                        buffer.append((char) next);
                    } else {
                        break;
                    }
                }
                String outputString = doFilter(buffer.toString());
                if (writer != null) {
                    if (outputString != null) {
                        writer.write(outputString);
                    }
                    writer.flush();
                }
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    public void writeLine(String line) throws IOException {
        if (writer != null && line != null) {
            writer.write(line);
            writer.write("\n");
            writer.flush();
        }
    }
    /**
     *  Returns the string value of the output if the
     *  connectOutput() method (with no arguments) is used.
     *  This method should be called after the run() method 
     *  returns.
     */
    public String toString() {
        return writer.toString();
    }
    /**
     *  Puts the string value of the output in the clipboard if the
     *  connectOutput() method (with no arguments) is used.
     *  This method should be called after the run() method 
     *  returns.
     */
    public void toClipboard() {
        setClipboardData(toString());
    }
}
