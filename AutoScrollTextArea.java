package filters;

import javax.swing.*;

public class AutoScrollTextArea extends JTextArea {
    protected boolean autoScroll = false;
    protected boolean isPaused = false;
    public AutoScrollTextArea(int rows, int columns) {
        super(rows, columns);
    }
    public void setIsPaused(boolean p) {
        isPaused = p;
    }
    public boolean getIsPaused() {
        return isPaused;
    }
    public void append(String s) {
        while (isPaused) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.append(s);
        if (autoScroll) {
            int length = getText().length();
            if (length > 0) {
                setCaretPosition(length - 1);
            }
        }
    }
    public void setAutoScroll(boolean b) {
        autoScroll = b;
    }
}
