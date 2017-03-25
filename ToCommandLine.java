package filters;

import java.io.*;

/**
 *  Convert to single line
 */
public class ToCommandLine extends Filter {
    public void setArguments(String argList) {
        // No arguments recognized.
    }
    public String doFilter(String input) {
        input = input.replaceAll(" \\\\ ", " ");
        if (input.charAt(input.length() - 1) == '\\') {
            input = input.substring(0, input.length() - 1);
        }
        return input;
    }
    public static void main(String[] args) {
        ToCommandLine filter = new ToCommandLine();
        filter.connectOutput(System.out);
        filter.connectInput(System.in);
        filter.start();
    }
}
