package filters;

import java.io.*;

/**
 *  Converts each windows full path into a cygwin style full path.
 */
public class ToCygwinFile extends Filter {
    public void setArguments(String argList) {
	// No arguments recognized.
    }
    public String doFilter(String inputString) {
	if (inputString == null) {
	    return "";
	}
	StringBuffer cygwinPath = new StringBuffer();
	int length = inputString.length();
	if (inputString.length() > 2) {
	    char root = inputString.charAt(0);
	    String path = inputString.substring(2, length);
	    cygwinPath.append("/cygdrive/");
	    cygwinPath.append(root);
	    cygwinPath.append(path.replace('\\', '/'));
	}
	return cygwinPath.toString() + "\n";
    }
    public static void main(String[] args) {
	ToCygwinFile filter = new ToCygwinFile();
	filter.connectOutput(System.out);
	filter.connectInput(System.in);
	filter.start();
    }
}
