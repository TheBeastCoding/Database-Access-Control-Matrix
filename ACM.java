package version2;

import java.util.ArrayList;

// ACM by subject for SQL server
public class ACM {
    // subject listing
    ArrayList<Subject> subjects;

    // object listing
    ArrayList<Object> objects;

    public ACM() {
        // initialize listings
        objects = new ArrayList<>();
        subjects = new ArrayList<>();

        // add root user to system
        Subject root = new Subject("root", "password", Role.ROOT);
        root.subjectCommands.add(new SubjectCommand("root", true, true));
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

    // print out access control matrix related to subjects (users)
    public void printSubjectACM() {
        System.out.println();
        System.out.println("SUBJECT ACM");

        System.out.print("      ");
        for(int i=0;i<subjects.size();i++) {
            System.out.print("   " + subjects.get(i).subjectName);
        }

        System.out.println();

        // for all subjects
        for(int i=0;i<subjects.size();i++) {
            Subject subject = subjects.get(i);

            System.out.print(subject.subjectName + " : ");

            // for all subject commands for each subject
            for(int j=0;j<subject.subjectCommands.size();j++) {
                SubjectCommand cmd = subject.subjectCommands.get(j);
                if(cmd.isOwner && cmd.hasControl) {
                    System.out.print("owner, control; ");
                } else if(cmd.isOwner) {
                    System.out.print("owner; ");
                } else if(cmd.hasControl) {
                    System.out.print("control; ");
                } else {
                    System.out.print("; ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    // print out access control matrix related to objects (tables)
    public void printObjectACM() {
        System.out.println();
        System.out.println("OBJECT ACM");

        System.out.print("      ");
        for(int i=0;i<objects.size();i++) {
            System.out.print("   " + objects.get(i).objectName);
        }

        System.out.println();

        // for all subjects
        for(int i=0;i<subjects.size();i++) {
            Subject subject = subjects.get(i);

            System.out.print(subject.subjectName + " : ");

            // for all object commands for each subject
            for(int j=0;j<subject.objectCommands.size();j++) {
                subject.objectCommands.get(j).printCommands();
            }
            System.out.println();
        }
        System.out.println();
    }

    // return the command listing on a given object for a specific user
    private ObjectCommand getObjectCommand(Subject subject, String object) {
        for(int i=0;i<subject.objectCommands.size();i++) {
            if(subject.objectCommands.get(i).object.equals(object)) {
                return subject.objectCommands.get(i);
            }
        }
        return null;
    }

    // return the command listing on a given subject for a specific user
    private SubjectCommand getSubjectCommand(Subject subject, String subjectName) {
        for(int i=0;i<subjects.size();i++) {
            if(subject.subjectCommands.get(i).subject.equals(subjectName)) {
                return subject.subjectCommands.get(i);
            }
        }
        return null;
    }

    // return a given subject
    private Subject getSubject(String username) {
        for(int i=0;i<subjects.size();i++) {
            if(subjects.get(i).subjectName.equals(username)) {
                return subjects.get(i);
            }
        }
        return null;
    }

    // get object
    private Object getObject(String name) {
        for(int i=0;i<objects.size();i++) {
            if(objects.get(i).objectName.equals(name)) {
                return objects.get(i);
            }
        }
        return null;
    }

    // check if object is in system
    private boolean verifyObject(String object) {
        for(int i=0;i<objects.size();i++) {
            if(objects.get(i).objectName.equals(object)) {
                return true;
            }
        }
        return false;
    }

    // check if subject is in system
    private boolean verifySubject(String subject) {
        for(int i=0;i<subjects.size();i++) {
            if(subjects.get(i).subjectName.equals(subject)) {
                return true;
            }
        }
        return false;
    }

    // check if provided password is valid
    private boolean verifyPassword(String username, String password) {
        Subject subject = getSubject(username);
        if(subject!=null) {
            if(subject.subjectPassword.equals(password)) {
                return true;
            }
        }
        return false;
    }

    // execute a TCL SQL command
    public boolean executeTCLCommand(String user, String password, boolean commit) {
        // verify password
        if(verifyPassword(user, password)) {
            // get user
            Subject subject = getSubject(user);

            // check if subject exists
            if(subject!=null) {
                // check priveledge
                if(subject.role.equals(Role.SECURITY_OFFICER) || subject.role.equals(Role.ADMINISTRATOR) || subject.equals(Role.ROOT)) {
                    // if TCL: COMMIT
                    if (commit) {
                        return true;
                    }

                    // IF TCL ROLLBACK
                    else {
                        return true;
                    }
                }
            }
        } else {
            System.out.println("Invalid Password");
        }
        return false;
    }

    // perform command on a given table
    public boolean executeObjectCommand(String user, String password, String object, Commands command) {
        // verify password
        if(verifyPassword(user, password)) {
            // get user
            Subject subject = getSubject(user);

            // check if subject exists
            if(subject!=null) {
                ObjectCommand objectCommand = getObjectCommand(subject, object);

                // check if user has command listing for given object
                if(objectCommand!=null) {
                    // determine action by command
                    switch (command) {
                        case SELECT:
                            if (objectCommand.select.current && objectCommand.select.allowed) {
                                return true;
                            }
                            break;
                        case DELETE:
                            if(objectCommand.delete.current && objectCommand.delete.allowed) {
                                return true;
                            }
                            break;
                        case INSERT:
                            if(objectCommand.insert.transferable && objectCommand.insert.allowed) {
                                return true;
                            }
                            break;
                    }
                }
            }
        } else {
            System.out.println("Invalid Password");
        }
        return false;
    }

    // revoke all
    public void revokeObjectRights(String user, String password, String otherUser, String object) {
        // verify password
        if(verifyPassword(user, password)) {
            // see if other subject exists
            if (verifySubject(otherUser)) {
                // get command listing for both users
                ObjectCommand otherUserRights = getObjectCommand(getSubject(otherUser), object);
                ObjectCommand userRights = getObjectCommand(getSubject(user), object);

                if(otherUserRights!=null && userRights!=null) {
                    // cant revoke owner's rights and user needs to have control or ownership for this action
                    if(!otherUserRights.isOwner && (userRights.hasControl || userRights.isOwner)) {
                        otherUserRights.revokeAll();
                    }
                }
            }
        } else {
            System.out.println("Invalid Password");
        }
    }

    // revoke one
    public void revokeObjectRights(String user, String password, String otherUser, String object, Commands command) {
        // verify password
        if(verifyPassword(user, password)) {
            // see if other subject exists
            if (verifySubject(otherUser)) {
                // get command listing for both users
                ObjectCommand otherUserRights = getObjectCommand(getSubject(otherUser), object);
                ObjectCommand userRights = getObjectCommand(getSubject(user), object);

                if(otherUserRights!=null && userRights!=null) {
                    // cant revoke owner's rights and user needs to have control or ownership for this action
                    if(!otherUserRights.isOwner && (userRights.hasControl || userRights.isOwner)) {
                        // based on command
                        switch (command) {
                            case INSERT:
                                otherUserRights.revokeInsert();
                                break;
                            case DELETE:
                                otherUserRights.revokeDelete();
                                break;
                            case SELECT:
                                otherUserRights.revokeSelect();
                                break;
                        }
                    }
                }
            }
        }
    }

    // transfer ownership
    public void transferSubjectOwnership(String fromUser, String fromPassword, String toUser) {
        // verify password
        if(verifyPassword(fromUser, fromPassword)) {
            // see if other subject exists
            if(verifySubject(toUser)) {
                // get users
                Subject from = getSubject(fromUser);
                Subject to = getSubject(toUser);

                // make sure both users exist in system
                if (from != null && to != null) {
                    // get command listing
                    SubjectCommand fromCommand = getSubjectCommand(from, toUser);
                    SubjectCommand toCommand = getSubjectCommand(to, toUser);

                    // see if command listing exists
                    if(fromCommand!=null && toCommand!=null) {
                        if(fromCommand.isOwner && (to.role.equals(Role.ADMINISTRATOR) || to.role.equals(Role.ROOT))) {
                            // update command listing
                            fromCommand.isOwner = false;
                            toCommand.isOwner = true;
                            toCommand.hasControl = true;
                        }
                    }
                }
            }
        }
    }

    // transfer ownership
    public void transferObjectOwnership(String fromUser, String fromPassword, String toUser, String object) {
        // verify password
        if(verifyPassword(fromUser, fromPassword)) {
            // see if other subject exists
            if(verifySubject(toUser)) {
                // get users
                Subject from = getSubject(fromUser);
                Subject to = getSubject(toUser);

                // make sure both users exist in system
                if (from != null && to != null) {
                    // get commands for given table
                    ObjectCommand fromObjCommand = getObjectCommand(from, object);
                    ObjectCommand toObjCommand = getObjectCommand(to, object);

                    // make sure each user has an object command
                    if(fromObjCommand!=null && toObjCommand!=null) {
                        // make sure from is an owner and to has a valid role (only admins and root)
                        if(fromObjCommand.isOwner && (to.role.equals(Role.ADMINISTRATOR) || to.role.equals(Role.ROOT))) {
                            // modify parameters
                            fromObjCommand.removeOwner();
                            toObjCommand.makeOwner();

                            // update ownership listing
                            Object obj = getObject(object);
                            obj.owner = toUser;
                        }
                    }
                }
            }
        }
    }

    // grant all possible commands
    public void grantObjectCommand(String fromUser, String fromPassword, String toUser, String object) {
        // verify password
        if(verifyPassword(fromUser, fromPassword)) {
            // see if other subject exists
            if(verifySubject(toUser)) {
                // get users
                Subject from = getSubject(fromUser);
                Subject to = getSubject(toUser);

                // make sure both users exist in system
                if (from != null && to != null) {
                    // get commands for given table
                    ObjectCommand fromObjCommand = getObjectCommand(from, object);
                    ObjectCommand toObjCommand = getObjectCommand(to, object);

                    // make sure each user has an object command
                    if(fromObjCommand!=null && toObjCommand!=null) {
                        // determine action by command
                        if (fromObjCommand.select.transferable && toObjCommand.select.allowed) {
                            toObjCommand.select.current = true;
                        }

                        if(fromObjCommand.delete.transferable && toObjCommand.delete.allowed) {
                            toObjCommand.delete.current = true;
                        }

                        if(fromObjCommand.insert.transferable && toObjCommand.insert.allowed) {
                            toObjCommand.insert.current = true;
                        }
                    } else {
                        System.out.println("One of the subjects does not have object command listing");
                    }
                } else {
                    System.out.println("One of the subjects is null");
                }
            } else {
                System.out.println("Subject does not exist");
            }
        } else {
            System.out.println("Invalid Password");
        }
    }

    // grant selective
    public void grantObjectCommand(String fromUser, String fromPassword, String toUser, String object, Commands command) {
        // verify password
        if(verifyPassword(fromUser, fromPassword)) {
            // see if other subject exists
            if(verifySubject(toUser)) {
                // get users
                Subject from = getSubject(fromUser);
                Subject to = getSubject(toUser);

                // make sure both users exist in system
                if (from != null && to != null) {
                    // get commands for given table
                    ObjectCommand fromObjCommand = getObjectCommand(from, object);
                    ObjectCommand toObjCommand = getObjectCommand(to, object);

                    // make sure each user has an object command
                    if(fromObjCommand!=null && toObjCommand!=null) {
                        // determine action by command
                        switch (command) {
                            case SELECT:
                                if (fromObjCommand.select.transferable && toObjCommand.select.allowed) {
                                    toObjCommand.select.current = true;
                                }
                                break;
                            case DELETE:
                                if(fromObjCommand.delete.transferable && toObjCommand.delete.allowed) {
                                    toObjCommand.delete.current = true;
                                }
                                break;
                            case INSERT:
                                if(fromObjCommand.insert.transferable && toObjCommand.insert.allowed) {
                                    toObjCommand.insert.current = true;
                                }
                                break;
                        }
                    } else {
                        System.out.println("One of the subjects is has not object command listing");
                    }
                } else {
                    System.out.println("One of the subjects is null");
                }
            } else {
                System.out.println("Subject does not exist");
            }
        } else {
            System.out.println("Invalid Password");
        }
    }

    public void addSubject(String userCreator, String passwordCreator, String user, String password, Role role) {
        if(verifyPassword(userCreator, passwordCreator)) {
            subjects.add(new Subject(user, password, role));

            // add all other subjects to new subject listing
            for (int i = 0; i < subjects.size() - 1; i++) {
                subjects.get(subjects.size()-1).subjectCommands.add(new SubjectCommand(subjects.get(i).subjectName, false, false));
            }

            // add new subject to all other subject listing
            for (int i = 0; i < subjects.size(); i++) {
                SubjectCommand subjectCommand = new SubjectCommand(user, false, false);

                // add ownership flag if owner
                if(subjects.get(i).subjectName.equals(userCreator)) {
                    subjectCommand.isOwner = true;
                    subjectCommand.hasControl = true;
                }
                // add ownership flag if owner
                if(subjects.get(i).subjectName.equals("root")) {
                    subjectCommand.hasControl = true;
                }
                subjects.get(i).subjectCommands.add(subjectCommand);
            }

            // add all objects to new subject
            for (int i = 0; i < objects.size(); i++) {
                subjects.get(subjects.size()-1).objectCommands.add(new ObjectCommand(objects.get(i).objectName, role, false, false));
            }
        } else {
            System.out.println("Invalid Password");
        }
    }

    public void removeSubject(String userDestroyer, String passwordDestroyer, String user) {
        if(verifyPassword(userDestroyer, passwordDestroyer)) {
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
        } else {
            System.out.println("Invalid Password");
        }
    }

    // add object (table)
    public void addObject(String userCreator, String passwordCreator, String objectName) {
        if(verifyPassword(userCreator, passwordCreator)) {
            objects.add(new Object(objectName, userCreator));

            // update lists
            for (int i = 0; i < subjects.size(); i++) {
                boolean isOwner = userCreator.equals(subjects.get(i).subjectName);
                ObjectCommand command = new ObjectCommand(objectName, subjects.get(i).role, isOwner, isOwner);

                // root has control over all
                if(subjects.get(i).subjectName.equals("root")) {
                    command.hasControl = true;
                }
                subjects.get(i).objectCommands.add(command);
            }
        } else {
            System.out.println("Invalid Password");
        }
    }

    // remove object (table)
    public void removeObject(String userCreator, String passwordCreator, String objectName) {
        // check password
        if(verifyPassword(userCreator, passwordCreator)) {
            // get subject
            Subject subject = getSubject(userCreator);

            // get command listing
            ObjectCommand objectCommand = getObjectCommand(subject, objectName);

            // see if object rights listing exists
            if(objectCommand!=null) {
                // check if owner of object
                if(objectCommand.isOwner) {
                    // find index of object
                    int index = -1;
                    for (int i = 0; i < objects.size(); i++) {
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
        } else {
            System.out.println("Invalid Password");
        }
    }
}
