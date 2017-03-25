package filters;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Convert extends Filter {
    protected String searchString;
    public Convert() {
    }
    public void setArguments(String argList) {
	StringTokenizer tokenizer = new StringTokenizer(argList);
	if (tokenizer.hasMoreTokens()) {
	    searchString = tokenizer.nextToken();
	}
    }
    public String doFilter(String input) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_\\-]+\\.jar");
        Matcher matcher = pattern.matcher(input);
	if (matcher.find()) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("<Dependency packageName=\""); buffer.append(matcher.group());
            buffer.append("\">\n");
            return buffer.toString();
        }
	return "";
    }
    public static void main(String[] args) {
	Convert filter = null;
	if (args == null || args.length == 0) {
	    filter = new Convert();
	    filter.setArguments("");
	    return;
	}
	filter = new Convert();
	filter.setArguments(args[0]);
	filter.connectOutput(System.out);
	filter.connectInput(System.in);
	filter.start();
    }
}
