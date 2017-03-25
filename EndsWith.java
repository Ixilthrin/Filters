package filters;

import java.io.*;
import java.util.*;

/**
 *  Supply an argument and this filter will
 *  output only the input lines that end with
 *  the given argument.
 */
public class EndsWith extends Filter {
    protected String searchString;
    public EndsWith() {
    }
    public void setArguments(String argList) {
        StringTokenizer tokenizer = new StringTokenizer(argList);
        if (tokenizer.hasMoreTokens()) {
            searchString = tokenizer.nextToken();
        }
    }
    public String doFilter(String input) {
        if (searchString != null && input != null 
            && input.trim().endsWith(searchString)) {
            return input + "\n";
        }
        return "";
    }
    public static void main(String[] args) {
        EndsWith filter = null;
        if (args == null || args.length == 0) {
            filter = new EndsWith();
            filter.setArguments("");
            return;
        }
        filter = new EndsWith();
        filter.setArguments(args[0]);
        filter.connectOutput(System.out);
        filter.connectInput(System.in);
        filter.start();
    }
}
