package version2;

import java.util.ArrayList;

public class ACM {
    // ACM by subject
    ArrayList<Subject> subjects;
    ArrayList<Object> objects;

    public ACM() {
        objects = new ArrayList<>();
        subjects = new ArrayList<>();

        // add root user to system
        Subject root = new Subject("root", "password", Role.ROOT);
        root.subjectCommands.add(new SubjectCommand("root"));
        subjects.add(root);
    }

    public void displayAllSubjects() {
        System.out.println("SUBJECTS");
        for(int i=0;i<subjects.size();i++) {
            System.out.println("  - " + subjects.get(i).subjectName);
        }
    }

    public void displayAllObjects() {
        System.out.println("OBJECTS");
        for(int i=0;i<objects.size();i++) {
            System.out.println("  - " + objects.get(i).objectName);
        }
    }

    public void printSubjectACM() {
        System.out.println();
        System.out.println("SUBJECT ACM");

        System.out.print("      ");
        for(int i=0;i<subjects.size();i++) {
            System.out.print(" " + subjects.get(i).subjectName);
        }

        System.out.println();

        for(int i=0;i<subjects.size();i++) {
            Subject subject = subjects.get(i);

            System.out.print(subject.subjectName + " : ");

            for(int j=0;j<subject.subjectCommands.size();j++) {
                System.out.print(subject.subjectCommands.get(j).placeholder + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printObjectACM() {
        System.out.println();
        System.out.println("OBJECT ACM");

        System.out.print("      ");
        for(int i=0;i<objects.size();i++) {
            System.out.print(" " + objects.get(i).objectName);
        }

        System.out.println();

        for(int i=0;i<subjects.size();i++) {
            Subject subject = subjects.get(i);

            System.out.print(subject.subjectName + " : ");

            for(int j=0;j<subject.objectCommands.size();j++) {
                System.out.print(subject.objectCommands.get(j).placeholder + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    private Subject getSubject(String username) {
        for(int i=0;i<subjects.size();i++) {
            if(subjects.get(i).subjectName.equals(username)) {
                return subjects.get(i);
            }
        }
        return null;
    }

    private boolean verifyPassword(String username, String password) {
        Subject subject = getSubject(username);
        if(subject!=null) {
            if(subject.subjectPassword.equals(password)) {
                return true;
            }
        }
        return false;
    }

    public void addSubject(String userCreator, String passwordCreator, String user, String password, Role role) {
        if(verifyPassword(userCreator, passwordCreator)) {
            subjects.add(new Subject(user, password, role));

            // add all other subjects to new subject listing
            for (int i = 0; i < subjects.size() - 1; i++) {
                subjects.get(subjects.size()-1).subjectCommands.add(new SubjectCommand(subjects.get(i).subjectName));
            }

            // add new subject to all other subject listing
            for (int i = 0; i < subjects.size(); i++) {
                subjects.get(i).subjectCommands.add(new SubjectCommand(user));
            }

            // add all objects to new subject
            for (int i = 0; i < objects.size(); i++) {
                subjects.get(subjects.size()-1).objectCommands.add(new ObjectCommand(objects.get(i).objectName));
            }
        }
    }

    public void removeSubject(String userCreator, String passwordCreator, String user) {
        if(verifyPassword(userCreator, passwordCreator)) {
            // find index
            int index = -1;
            for (int i = 0; i < subjects.size(); i++) {
                // find index of user
                if(subjects.get(i).subjectName.equals(user)) {
                    index = i;
                }
            }

            // if user found
            if(index!=-1) {
                // remove user from each user's list
                for (int i = 0; i < subjects.size(); i++) {
                    subjects.get(i).subjectCommands.remove(index);
                }

                // finally remove user from main user list
                subjects.remove(index);
            }
        }
    }

    public void addObject(String userCreator, String passwordCreator, String objectName) {
        if(verifyPassword(userCreator, passwordCreator)) {
            objects.add(new Object(objectName));

            // update lists
            for (int i = 0; i < subjects.size(); i++) {
                subjects.get(i).objectCommands.add(new ObjectCommand(objectName));
            }
        }
    }

    public void removeObject(String userCreator, String passwordCreator, String objectName) {
        if(verifyPassword(userCreator, passwordCreator)) {
            // update lists
            int index = -1;
            for (int i = 0; i < objects.size(); i++) {
                // find index of object
                if(objects.get(i).objectName.equals(objectName)) {
                    index = i;
                }
            }

            // if flag raised
            if(index!=-1) {
                // remove object from each user's list
                for (int i = 0; i < subjects.size(); i++) {
                    subjects.get(i).objectCommands.remove(index);
                }

                // finally remove user from main user list
                objects.remove(index);
            }
        }
    }
}
