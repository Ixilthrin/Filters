package filters;

import java.io.*;
import java.util.*;

/**
 *   To be applied to output from command 'cvs diff | grep diff'
 *   Output a list of filenames.
 */ 
public class CvsDiffFilter extends Filter {
    public CvsDiffFilter() {
    }
    public void setArguments(String argList) {
        // No arguments
    }
    public String doFilter(String input) {
        if (input.startsWith("diff") && !input.endsWith(" ")) {
            int index = input.lastIndexOf(' ');
            return input.substring(index + 1) + "\n";
        }
        return "";
    }
    public static void main(String[] args) {
        CvsDiffFilter filter = new CvsDiffFilter();
        filter.connectOutput(System.out);
        filter.connectInput(System.in);
        filter.start();
    }
}
