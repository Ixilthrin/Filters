package filters;

import java.io.*;

/**
 *  Convert to single line - basically change newline to space
 */
public class ToArgList extends Filter {
    public void setArguments(String argList) {
	// No arguments recognized.
    }
    public String doFilter(String input) {
	return input + " ";
    }
    public static void main(String[] args) {
	ToArgList filter = new ToArgList();
	filter.connectOutput(System.out);
	filter.connectInput(System.in);
	filter.start();
    }
}
