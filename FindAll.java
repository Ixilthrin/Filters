package filters;

import java.io.*;

/**
 *  Recursively list all subfiles.
 */
public class FindAll extends Filter {
    public void setArguments(String argList) {
	// No arguments recognized.
    }
    public static boolean showFullPath = true;
    /**
     *  Ignore input and return nothing.
     *  Call writeFile to write each file recursively starting
     *  with current working directory.
     */
    public String doFilter(String input) {
	if (cwd != null) {
	    writeFile(cwd);
	}
	return "";
    }
    /**
     *   Recuresively print each file.
     */
    public void writeFile(File file) {
	try {
	    writeLine(file.toString());
	    if (file.isDirectory()) {
		File list[] = file.listFiles();
		if (list != null) {
		    for (int i = 0; i < list.length; ++i) {
			writeFile(list[i]);
		    }
		}
	    }
	} catch (IOException e) {
	    // NYI: Show message.
	    e.printStackTrace();
	}
    }
    public static void main(String[] args) {
	FindAll filter = new FindAll();
	String directory = System.getProperty("user.dir");
	filter.setDirectory(directory);
	filter.connectOutput(System.out);
	filter.start();
    }
}
