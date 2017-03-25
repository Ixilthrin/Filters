package filters;

import java.io.*;

/**
 *  Mainly used as an example and starting point for 
 *  chaining filters together programmatically.
 */
public class TempFilter extends Filter {
    public void setArguments(String argList) {
	// No arguments recognized.
    }
    public String doFilter(String in) {
	if (in.trim().equals("")) {
	    return "";
	}
	return "wizard." + in + "\n";
    }
    public static void main(String args[]) {
	TempFilter addWizard = new TempFilter();
	ClipboardInput input = new ClipboardInput();
	ClipboardOutput output = new ClipboardOutput();
	RemoveWebtoolDot removeDot = new RemoveWebtoolDot();
	EndsWith ends = new EndsWith();
	ends.setArguments("t");
	ToArgList list = new ToArgList();

	input.connectOutput(removeDot);
	removeDot.connectOutput(addWizard);
	addWizard.connectOutput(ends);
	ends.connectOutput(list);
	list.connectOutput(output);

	input.start();
    }
}
