package model.enums;

public enum MatchResult {
    WIN("Win"),
    LOSS("Loss");

    private final String displayName;

    MatchResult(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
