package filters;

import java.io.*;

/**
 *  For each line which represents a directory,
 *  output the list of subfiles.
 */
public class GetSubFiles extends Filter {
    public void setArguments(String argList) {
	// No arguments recognized.
    }
    public static boolean showFullPath = true;
    public String doFilter(String input) {
	StringBuffer buffer = new StringBuffer();
	File file = new File(input);
	if (file.exists()) {
	    if (file.isDirectory()) {
		File list[] = file.listFiles();
		for (int i = 0; i < list.length; ++i) {
		    if (showFullPath) {
			buffer.append(list[i]);
			buffer.append('\n');
		    } else {
			buffer.append(list[i].getName());
			buffer.append('\n');
		    }
		}
	    } else {
		if (showFullPath) {
		    buffer.append(file);
		    buffer.append('\n');
		} else {
		    buffer.append(file.getName());
		    buffer.append('\n');
		}
	    }
	}
	return buffer.toString();
    }
    public static void main(String[] args) {
	GetSubFiles filter = new GetSubFiles();
	filter.connectOutput(System.out);
	filter.connectInput(System.in);
	filter.start();
    }
}
