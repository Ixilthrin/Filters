package filters;

import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 *  A special Filter that executes a command.
 *  The input of this filter is filtered and sent
 *  to the process.  The output from the process
 *  becomes the output of this Filter.
 */
public class CommandFilter extends Filter {
    protected String command;
    protected Process process;
    protected PrintWriter processWriter;
    protected BufferedReader processReader;
    protected BufferedReader errorReader;
    protected Writer errorWriter;
    protected String environment[];
    protected boolean processIsTerminated = true;
    public CommandFilter() {
    }
    public void setArguments(String argList) {
        try {
            command = toSingleLine(argList);
        } catch (IOException e) {
            command = argList;
        }
    }
    public static String toSingleLine(String commandLine) throws IOException {
        String result = commandLine;
        result = result.replaceAll("\\\\\n", " ");
        result = result.replaceAll(" \\\\ ", " ");
        result = result.replaceAll("\\\\\r\n", " ");
        result = result.replaceAll("\n", " ");
        result = result.replaceAll("\r", "");
        return result;
    }
    /**
     *  The input text is echoed and sent
     *  to the underlying process.
     *  Subclasses can override this to filter
     *  input sent to the process.
     */
    public String doFilter(String in) {
        return in + "\n";
    }
    public void run() {
        environment = null;  // Use parent process environment.
        if (command == null || command.trim().equals("")) {
            JOptionPane.showMessageDialog(null, "No command specified in " + this.getClass().toString());
        } else {
            try {
                process = Runtime.getRuntime().exec(command, environment, cwd);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error executing the command: " + command);
            }
        }
        if (process == null) {
            // The process did not work, so act
            // like an ordinary filter.
            super.run();
            return;
        }
        processIsTerminated = false;
        if (outputFilter != null) {
            outputFilter.start();
        }

        OutputStream ostream = process.getOutputStream();
        processWriter = new PrintWriter(ostream);
        InputStream istream = process.getInputStream();
        processReader = new BufferedReader(new InputStreamReader(istream));
        InputStream estream = process.getErrorStream();
        errorReader = new BufferedReader(new InputStreamReader(estream));

        try {
            Thread readFromProcess = new Thread() {
                public void run() {
                    try {
                        while (true) {
                            String line = processReader.readLine();
                            if (line == null) {
                                writer.flush();
                                writer.close();
                                processReader.close();
                                break;
                            }
                            writer.write(line);
                            writer.write("\n");
                            writer.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            // For now, suppress any error output.
            Thread readErrorFromProcess = new Thread() {
                public void run() {
                    try {
                        while (true) {
                            String line = errorReader.readLine();
                            if (line == null) {
                                if (errorWriter != null) {
                                    errorWriter.flush();
                                    errorWriter.close();
                                }
                                errorReader.close();
                                break;
                            }
                            if (errorWriter != null) {
                                errorWriter.write(line);
                                errorWriter.write("\n");
                                errorWriter.flush();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            readFromProcess.start();
            readErrorFromProcess.start();
            if (reader instanceof BufferedReader) {
                while (true) {
                    String inputLine = null;
                    inputLine = ((BufferedReader) reader).readLine();
                    if (inputLine == null) break;
                    String outputString = doFilter(inputLine);
                    if (outputString != null) {
                        processWriter.println(outputString);
                        processWriter.flush();
                    }
                }
            } else if (reader != null) {
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
                if (outputString != null && writer != null) {
                    writer.write(outputString);
                }
                processWriter.flush();
            }
            processWriter.close();
        } catch (IOException e) {
            System.err.println(e);
        }
        if (process != null) {
            // Wait for the process to exit in a way which 
            // allows us to kill it.
            boolean processGoing = true;
            while (processGoing) {
                try {
                    if (process == null) {
                        processGoing = false;
                    } else {
                        process.exitValue();  // Throws exception is not exited yet.
                    }
                } catch (IllegalThreadStateException e) {
                    // Thread has not yet excited.
                    try {
                        Thread.sleep(1000);
                        if (processIsTerminated) {
                            processGoing = false;
                        }
                    } catch (InterruptedException e2) {
                        // Do nothing here.
                    }
                    continue;
                }
                processGoing = false;
            }
        }
    }
    public void terminateProcess() {
        processIsTerminated = true;
        if (process != null) {
            process.destroy();
        }
    }
    public void connectErrorOutput(OutputStream output) {
        errorWriter = new PrintWriter(output);
    }
    public void connectErrorOutput(JTextArea area) {
        errorWriter = new TextAreaWriter(area);
    }
    public void connectOutput(File output) {
        try {
            errorWriter = new BufferedWriter(new FileWriter(output));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    public static void main(String[] args) {
        if (args.length > 1 && args[0].equals("-test")) {
            try {
            System.out.println(toSingleLine(args[1]));
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else {
            if (args == null || args.length == 0) {
                System.out.println("No command specified.");
                return;
            }
            StringBuffer command = new StringBuffer();
            for (int i = 0; i < args.length; ++i) {
                command.append(args[i]);
                command.append(" ");
            }
            CommandFilter filter = new CommandFilter();
            filter.setArguments(command.toString());
            filter.connectInput(System.in);
            filter.connectOutput(System.out);
            filter.start();
        }
    }
}
