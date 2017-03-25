package filters;

public class Update extends CommandFilter {
    public Update() {
	super();
    }
    public void setArguments(String argList) {
	command = "sh c:/bin/up";
    }
}
