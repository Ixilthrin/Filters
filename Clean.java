package filters;

public class Clean extends CommandFilter {
    public Clean() {
	super();
    }
    public void setArguments(String argList) {
	command = "sh c:/bin/clean";
    }
}
