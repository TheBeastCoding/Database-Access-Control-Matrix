package version2;

import java.util.ArrayList;

public class Subject {
    // characteristics
    String subjectName;
    String subjectPassword;
    Role role;

    // command listing
    ArrayList<SubjectCommand> subjectCommands;
    ArrayList<ObjectCommand> objectCommands;

    public Subject(String subjectName, String subjectPassword, Role role) {
        this.role = role;
        this.subjectName = subjectName;
        this.subjectPassword = subjectPassword;

        subjectCommands = new ArrayList<>();
        objectCommands = new ArrayList<>();
    }
}
