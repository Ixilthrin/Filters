package filters;

import java.io.*;

/**
 *  Remove "webtool." from beginning of each line.
 */
public class RemoveWebtoolDot extends Filter {
    public void setArguments(String argList) {
	// No arguments recognized.
    }
    public String doFilter(String inputString) {
	int dotIndex = inputString.indexOf(".");
	if (dotIndex == -1 || inputString.length() < dotIndex + 1) {
	    return inputString + "\n";
	}
	return inputString.substring(dotIndex + 1, 
				     inputString.length()) + "\n";
    }
    public static void main(String[] args) {
	RemoveWebtoolDot filter = new RemoveWebtoolDot();
	filter.connectOutput(System.out);
	filter.connectInput(System.in);
	filter.start();
    }
}
