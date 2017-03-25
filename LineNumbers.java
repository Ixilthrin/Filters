package filters;

import java.io.*;

/**
 *  Add line numbers to the output.
 */
public class LineNumbers extends Filter {
    public void setArguments(String argList) {
	// No arguments recognized.
    }
    int currentLineNumber = 0;
    public String doFilter(String inputString) {
	++currentLineNumber;
	return new Integer(currentLineNumber).toString() 
	    + ". " + inputString + "\n";
    }
    public void run() {
	super.run();
    }
    public static void main(String[] args) {
	LineNumbers filter = new LineNumbers();
	filter.connectInput(System.in);
	filter.connectOutput(System.out);
	filter.start();
    }
}
