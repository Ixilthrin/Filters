package filters;

import java.io.*;

/**
 *  Converts each full path in a list to a simple filename.
 */
public class ToFileName extends Filter {
    public void setArguments(String argList) {
	// No arguments recognized.
    }
    public String doFilter(String inputString) {
	File file = new File(inputString);
	if (file.exists()) {
	    return file.getName() + "\n";
	}
	return "";
    }
    public static void main(String[] args) {
	ToFileName filter = new ToFileName();
	filter.connectOutput(System.out);
	filter.connectInput(System.in);
	filter.start();
    }
}
