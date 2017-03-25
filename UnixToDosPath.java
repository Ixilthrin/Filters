package filters;

import java.io.*;

/**
 *  Converts each windows path into a unix style path.
 */
public class UnixToDosPath extends Filter {
    public void setArguments(String argList) {
	// No arguments recognized.
    }
    public String doFilter(String inputString) {
	if (inputString == null) {
	    return "";
	}
	inputString = inputString.replace('/', '\\');
	return inputString + "\n";
    }
    public static void main(String[] args) {
	UnixToDosPath filter = new UnixToDosPath();
	filter.connectOutput(System.out);
	filter.connectInput(System.in);
	filter.start();
    }
}
