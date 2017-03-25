package filters;

import java.io.*;

/**
 *  Add webtool. to beginning of line.
 */
public class AddWebtoolDot extends Filter {
    public String doFilter(String inputString) {
	return "webtool." + inputString + "\n";
    }
    public void setArguments(String argList) {
	// No arguments recognized.
    }
    public static void main(String[] args) {
	AddWebtoolDot filter = new AddWebtoolDot();
	filter.connectOutput(System.out);
	filter.connectInput(System.in);
	filter.start();
    }
}
