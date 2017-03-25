package filters;

import java.io.*;

/**
 *  Takes a list of files as input and outputs
 *  only those that represent Java source files.
 */
public class ContainsJavaFile extends Filter {
    public void setArguments(String argList) {
	// No arguments recognized.
    }
    /**
     *  Takes a list of files as input and outputs
     *  only those that represent Java source files.
     */
    public String doFilter(String input) {
	File file = new File(input);
	if (file.exists() && containsJavaFile(file)) {
	    return input + "\n";
	}
	return "";
    }
    public static void main(String[] args) {
	ContainsJavaFile filter = new ContainsJavaFile();
	filter.connectOutput(System.out);
	filter.connectInput(System.in);
	filter.start();
    }

    public static boolean containsJavaFile(File file) {
	boolean containsJavaFile = false;
	if (file.isDirectory()) {
	    File list[] = file.listFiles();
	    for (int i=0; i < list.length; ++i) {
		boolean ret = containsJavaFile(list[i]);
		if (ret) {
		    return true;
		}
	    }
	} else {
	    if (file.getName().endsWith(".java")) {
		return true;
	    }
	}
	return false;
    }
}
