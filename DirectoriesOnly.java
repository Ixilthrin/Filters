package filters;

import java.io.*;

/**
 *  Takes a list of files as input and outputs
 *  only those that represent directories.
 */
public class DirectoriesOnly extends Filter {
    public void setArguments(String argList) {
	// No arguments recognized.
    }
    /**
     *  Takes a list of files as input and outputs
     *  only those that represent directories.
     */
    public String doFilter(String input) {
	File file = new File(input);
	if (file.exists() && file.isDirectory()) {
	    return input + "\n";
	}
	return "";
    }
    public static void main(String[] args) {
	DirectoriesOnly filter = new DirectoriesOnly();
	filter.connectOutput(System.out);
	filter.connectInput(System.in);
	filter.start();
    }
}
