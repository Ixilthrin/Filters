package filters;

import java.io.*;

/**
 *  Echo the input to the output.
 *  Can be used for testing.
 */
public class Echo extends Filter {
    public void setArguments(String argList) {
        // No arguments recognized.
    }
    public String doFilter(String inputString) {
        return inputString + "\n";
    }
    public void run() {
        super.run();
    }
    /**
     *  The following can be used as a pattern for Filter
     *  objects which use command-line piping:
     *
     *  <code>Echo filter = new Echo();
     *  filter.connectInput(System.in);
     *  filter.connectOutput(System.out);
     *  filter.start();</code>
     */
    public static void main(String[] args) {
        Echo filter = new Echo();
        filter.connectInput(System.in);
        filter.connectOutput(System.out);
        filter.start();
    }
}
