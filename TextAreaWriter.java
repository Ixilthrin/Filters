package filters;

import java.io.*;
import java.lang.reflect.*;
import javax.swing.*;

/**
 *  Appends text written to this Writer to the
 *  given JTextArea.
 */
public class TextAreaWriter extends Writer {
    JTextArea textArea;
    public TextAreaWriter(JTextArea area) {
	super();
	textArea = area;
    }
    public void close() {
	// Nothing to be done
    }
    public void flush() {
	// Nothing to be done
    }
    public void write(char []cbuf) {
	String s = new String(cbuf);
	textArea.append(s);
    }
    public void write(char []cbuf, int off, int len) {
	String s = new String(cbuf, off, len);
	textArea.append(s);
    }
    public void write(int c) {
	char []cbuf = new char[1];
	cbuf[0] = (char) c;
	String s = new String(cbuf);
	textArea.append(s);
    }
    public void write(String s) {
	textArea.append(s);
    }
    public void write(String s, int off, int len) {
	String sub = s.substring(off, len - off);
	textArea.append(sub);
    }
}

