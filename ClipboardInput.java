package filters;

import java.io.*;

/**
 *  A special filter with input received from clipboard text.
 */
public class ClipboardInput extends Filter {
    public ClipboardInput() {
	super();
	connectInputToClipboard();
    }
    public void setArguments(String argList) {
	// No arguments recognized.
    }
    public String doFilter(String inputString) {
	return inputString + "\n";
    }
    public static void main(String[] args) {
	ClipboardInput filter = new ClipboardInput();
	filter.connectInputToClipboard();
	filter.connectOutput(System.out);
	filter.start();
    }
}
