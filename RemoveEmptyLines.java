package filters;

import java.io.*;

/**
 * 
 */
public class RemoveEmptyLines extends Filter {
    public void setArguments(String argList) {
        // No arguments recognized.
    }
    public String doFilter(String input) {
        if (input.trim().equals("")) {
            return null;
        }
        return input + "\n";
    }
    public static void main(String[] args) {
        RemoveEmptyLines filter = new RemoveEmptyLines();
        filter.connectOutput(System.out);
        filter.connectInput(System.in);
        filter.start();
    }
}
