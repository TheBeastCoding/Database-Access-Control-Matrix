package version2;

public class ObjectCommand {
    String object;
    boolean isOwner;
    boolean hasControl;
    CommandPair select;
    CommandPair insert;
    CommandPair delete;

    public ObjectCommand(String object, Role role, boolean isOwner, boolean hasControl) {
        this.object = object;
        this.isOwner = isOwner;
        this.hasControl = hasControl;

        // set ability by role
        if(role.equals(Role.ADMINISTRATOR) || role.equals(Role.REGULAR_USER)) {
            select = new CommandPair(true, false, false);
            insert = new CommandPair(true, false, false);
            delete = new CommandPair(true, false, false);
        }
        else if(role.equals(Role.ROOT)) {
            select = new CommandPair(true, true, true);
            insert = new CommandPair(true, true, true);
            delete = new CommandPair(true, true, true);
        }
        else {
            select = new CommandPair(false, false, false);
            insert = new CommandPair(false, false, false);
            delete = new CommandPair(false, false, false);
        }

        // owner has additional rights and transferability
        if(isOwner || hasControl) {
            setCurrentAndTransferable();
        }
    }

    public void revokeSelect() {
        hasControl = false;
        select.transferable = false;
        select.current = false;
    }

    public void revokeInsert() {
        hasControl = false;
        insert.transferable = false;
        insert.current = false;
    }

    public void revokeDelete() {
        hasControl = false;
        delete.transferable = false;
        delete.current = false;
    }

    public void revokeAll() {
        hasControl = false;
        select.transferable = false;
        select.current = false;
        insert.transferable = false;
        insert.current = false;
        delete.transferable = false;
        delete.current = false;
    }

    public void removeOwner() {
        isOwner = false;
    }

    public void makeOwner() {
        isOwner = true;
        hasControl = true;
        setCurrentAndTransferable();
    }

    private void setCurrentAndTransferable() {
        select.transferable = true;
        select.current = true;
        insert.transferable = true;
        insert.current = true;
        delete.transferable = true;
        delete.current = true;
    }

    public void printCommands() {
        String output = "";
        if (isOwner) {
            output += "owner, control; ";
        } else if(hasControl) {
            output+="control; ";
        } else if(isOwner) {
            output+="owner; ";
        } else {
            if (select.current) {
                output += "select";
            }
            if (select.transferable) {
                output += "*";
            }
            if (insert.current) {
                output += " insert";
            }
            if (insert.transferable) {
                output += "*";
            }
            if (delete.current) {
                output += " delete";
            }
            if (delete.transferable) {
                output += "*";
            }
            output += "; ";
        }
        System.out.print(output);
    }
}
