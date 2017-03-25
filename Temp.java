package filters;

public class Temp extends Filter {
    public String doFilter(String in) {
	int i = in.indexOf("1");
	if (i != -1) {
	    return in.substring(0, i-1).trim() + "\n";
	}
	return "";
    }
    public void setArguments(String args) {
    }
}
