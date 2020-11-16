package version2;

public class CommandPair {
    boolean allowed;
    boolean current;
    boolean transferable;

    public CommandPair(boolean allowed, boolean current, boolean transferable) {
        this.allowed = allowed;
        this.current = current;
        this.transferable = transferable;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public boolean isTransferable() {
        return transferable;
    }

    public boolean isCurrent() {
        return current;
    }
}
