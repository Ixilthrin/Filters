package filters;

public class Find extends CommandFilter {
    public Find() {
        super();
    }
    public void setArguments(String argList) {
        command = "c:/cygwin/bin/find -iname " + argList;
    }
}
