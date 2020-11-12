package version2;

public class ObjectCommand {
    String object;
    boolean isOwner = false;
    String select = "N/A";
    String insert = "";
    String delete = "";

    public ObjectCommand(String object) {
        this.object = object;
    }
}
