package filters;

import java.io.*;
import java.util.*;

/**
 *  Supply an argument and this filter will
 *  output only the input lines that do not contain
 *  the given argument.
 */
public class DoesNotContain extends Filter {
    protected String searchString;
    public DoesNotContain() {
    }
    public void setArguments(String argList) {
	StringTokenizer tokenizer = new StringTokenizer(argList);
	if (tokenizer.hasMoreTokens()) {
	    searchString = tokenizer.nextToken();
	}
    }
    public String doFilter(String input) {
	if (input.trim().indexOf(searchString) == -1) {
	    return input + "\n";
	}
	return "";
    }
    public static void main(String[] args) {
	DoesNotContain filter = null;
	if (args == null || args.length == 0) {
	    filter = new DoesNotContain();
	    filter.setArguments("");
	    return;
	}
	filter = new DoesNotContain();
	filter.setArguments(args[0]);
	filter.connectOutput(System.out);
	filter.connectInput(System.in);
	filter.start();
    }
}
