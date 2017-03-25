package filters;

import java.io.*;
import java.util.*;

/**
 *   To be applied to output from the mm command
 */ 
public class MmFilter extends Filter {
    public void setArguments(String argList) {
        // No arguments
    }
    public String doFilter(String input) {
        if (input.indexOf("Entering dir") != -1) {
            input = "\n\nENTERING DIRECTORY\n" + input;
        } else if (input.indexOf("Leaving dir") != -1) {
            input = "\n\nLEAVING DIRECTORY\n" + input;
        } else if (input.indexOf(" error ") != -1) {
            input = "\n\nERROR !!!!\n" + input;
        }
        return input + "\n";
    }
    public static void main(String[] args) {
        MmFilter filter = new MmFilter();
        filter.connectOutput(System.out);
        filter.connectInput(System.in);
        filter.start();
    }
}
