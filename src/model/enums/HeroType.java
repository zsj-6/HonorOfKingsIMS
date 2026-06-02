package model.enums;

public enum HeroType {
    TANK("Tank"),
    FIGHTER("Fighter"),
    ASSASSIN("Assassin"),
    MAGE("Mage"),
    MARKSMAN("Marksman"),
    SUPPORT("Support");

    private final String displayName;

    HeroType(String displayName) {
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
