package version2;

public class SubjectCommand {
    String subject;
    boolean isOwner;
    boolean hasControl;

    public SubjectCommand(String subject, boolean isOwner, boolean hasControl) {
        this.subject = subject;
        this.isOwner = isOwner;
        this.hasControl = hasControl;
    }
}