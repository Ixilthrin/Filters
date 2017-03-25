package filters;

import java.io.*;

/**
 *  A special filter with output sent to the clipboard.
 */
public class ClipboardOutput extends Filter {
    public ClipboardOutput() {
	super();
	connectOutput();
    }
    public void setArguments(String argList) {
	// No arguments recognized.
    }
    public String doFilter(String inputString) {
	return inputString + "\n";
    }
    /**
     *  Calls super class's run() method then sends
     *  the output to the clipboard using toClipboard().
     */
    public void run() {
	super.run();
	toClipboard();
    }
    public static void main(String[] args) {
	ClipboardOutput filter = new ClipboardOutput();
	filter.connectInput(System.in);
	filter.connectOutput();
	filter.start();
    }
}
