package filters;

import java.io.*;

/**
 *  Generate a System.out.println statement. 
 */
public class MakePrintStatement extends Filter {
    public void setArguments(String argList) {
	// No arguments recognized.
    }
    public String doFilter(String inputString) {
        return "System.out.println(\"" + inputString + "=\"+" + inputString + ");";
    }
    public void run() {
	super.run();
    }
    public static void main(String[] args) {
	MakePrintStatement filter = new MakePrintStatement();
	filter.connectInput(System.in);
	filter.connectOutput(System.out);
	filter.start();
    }
}
