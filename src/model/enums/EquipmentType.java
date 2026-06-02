package model.enums;

public enum EquipmentType {
    ATTACK("Attack"),
    DEFENSE("Defense"),
    MAGIC("Magic"),
    MOVEMENT("Movement");

    private final String displayName;

    EquipmentType(String displayName) {
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
