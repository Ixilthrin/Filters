package filters;

import java.io.*;
import java.util.*;

/**
 *  Supply an argument and this filter will
 *  output only the input lines that start with
 *  the given argument.
 */
public class StartsWith extends Filter {
    protected String searchString;
    public StartsWith() {
    }
    public void setArguments(String argList) {
	StringTokenizer tokenizer = new StringTokenizer(argList);
	if (tokenizer.hasMoreTokens()) {
	    searchString = tokenizer.nextToken();
	}
    }
    public String doFilter(String input) {
	if (input.trim().startsWith(searchString)) {
	    return input + "\n";
	}
	return "";
    }
    public static void main(String[] args) {
	StartsWith filter = null;
	if (args == null || args.length == 0) {
	    filter = new StartsWith();
	    filter.setArguments("");
	    return;
	}
	filter = new StartsWith();
	filter.setArguments(args[0]);
	filter.connectOutput(System.out);
	filter.connectInput(System.in);
	filter.start();
    }
}
